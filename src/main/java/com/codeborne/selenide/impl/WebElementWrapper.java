package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Proxy;

import static com.codeborne.selenide.impl.Plugins.inject;

@ParametersAreNonnullByDefault
public class WebElementWrapper extends WebElementSource {
  public static SelenideElement wrap(Driver driver, WebElement element) {
    return wrap(SelenideElement.class, driver, element);
  }

  public static <T extends SelenideElement> T wrap(Class<T> clazz, Driver driver, WebElement element) {
    return wrap(clazz, driver, element, null);
  }

  public static SelenideElement wrap(Driver driver, WebElement element, @Nullable String searchCriteria) {
    return wrap(SelenideElement.class, driver, element, searchCriteria);
  }

  @SuppressWarnings("unchecked")
  public static <T extends SelenideElement> T wrap(Class<T> clazz, Driver driver,
                                                   WebElement element, @Nullable String searchCriteria) {
    return clazz.isInstance(element) ?
      clazz.cast(element) :
      (T) Proxy.newProxyInstance(
        element.getClass().getClassLoader(), new Class<?>[]{clazz},
        new SelenideElementProxy<>(new WebElementWrapper(driver, element, searchCriteria)));
  }

  private final ElementDescriber describe = inject(ElementDescriber.class);
  private final Driver driver;
  private final WebElement delegate;
  @Nullable private final String searchCriteria;

  protected WebElementWrapper(Driver driver, WebElement delegate, @Nullable String searchCriteria) {
    this.driver = driver;
    this.delegate = delegate;
    this.searchCriteria = searchCriteria;
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
    return searchCriteria != null ? searchCriteria : describe.briefly(driver, delegate);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return getAlias().getOrElse(() -> describe.fully(driver(), delegate));
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Driver driver() {
    return driver;
  }
}
