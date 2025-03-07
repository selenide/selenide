package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.ex.ElementNotFound;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;

import static com.codeborne.selenide.Condition.exist;
import static java.lang.Thread.currentThread;

public class JSElementFinder extends WebElementSource {
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
  public Driver driver() {
    return driver;
  }

  @Override
  public WebElement getWebElement() throws NoSuchElementException, IndexOutOfBoundsException {
    WebElement webElement = driver.executeJavaScript(js, parent.getWebElement());
    if (webElement == null) {
      throw new NoSuchElementException("Cannot locate an element " + description());
    }
    return webElement;
  }

  @Override
  public ElementNotFound createElementNotFoundError(WebElementCondition condition, @Nullable Throwable cause) {
    parent.checkCondition("", exist, false);
    return super.createElementNotFoundError(condition, cause);
  }

  @Override
  public String getSearchCriteria() {
    return description;
  }

  @Override
  public String toString() {
    return "{" + description() + '}';
  }
}
