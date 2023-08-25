package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Proxy;

import static com.codeborne.selenide.Condition.exist;
import static java.lang.Thread.currentThread;

@ParametersAreNonnullByDefault
public class JSElementFinder extends WebElementSource {
  @CheckReturnValue
  @Nonnull
  @SuppressWarnings("unchecked")
  public static <T extends SelenideElement> T wrap(Class<T> clazz, Driver driver,
                                                   String description, WebElementSource parent, String js) {
    return (T) Proxy.newProxyInstance(
      currentThread().getContextClassLoader(),
      new Class<?>[]{clazz},
      new SelenideElementProxy<>(new JSElementFinder(driver, description, parent, js)));
  }

  private final Driver driver;
  private final String description;
  private final WebElementSource parent;
  private final String js;

  JSElementFinder(Driver driver, String description, WebElementSource parent, String js) {
    this.driver = driver;
    this.description = description;
    this.parent = parent;
    this.js = js;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Driver driver() {
    return driver;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebElement getWebElement() throws NoSuchElementException, IndexOutOfBoundsException {
    WebElement webElement = driver.executeJavaScript(js, parent.getWebElement());
    if (webElement == null) {
      throw new NoSuchElementException("Cannot locate an element " + description());
    }
    return webElement;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public ElementNotFound createElementNotFoundError(Condition condition, Throwable cause) {
    parent.checkCondition("", exist, false);
    return super.createElementNotFoundError(condition, cause);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String getSearchCriteria() {
    return description;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return "{" + description() + '}';
  }
}
