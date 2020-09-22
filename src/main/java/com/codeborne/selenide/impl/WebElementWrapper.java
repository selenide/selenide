package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Proxy;

import static com.codeborne.selenide.impl.Plugins.inject;

@ParametersAreNonnullByDefault
public class WebElementWrapper extends WebElementSource {
  public static SelenideElement wrap(Driver driver, WebElement element) {
    return element instanceof SelenideElement ?
        (SelenideElement) element :
        (SelenideElement) Proxy.newProxyInstance(
            element.getClass().getClassLoader(), new Class<?>[]{SelenideElement.class},
            new SelenideElementProxy(new WebElementWrapper(driver, element)));
  }

  private final ElementDescriber describe = inject(ElementDescriber.class);
  private final Driver driver;
  private final WebElement delegate;

  protected WebElementWrapper(Driver driver, WebElement delegate) {
    this.driver = driver;
    this.delegate = delegate;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebElement getWebElement() {
    return delegate;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String getSearchCriteria() {
    return describe.briefly(driver, delegate);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return describe.fully(driver(), delegate);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Driver driver() {
    return driver;
  }
}
