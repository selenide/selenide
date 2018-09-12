package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;

public class WebElementWrapper extends WebElementSource {
  public static SelenideElement wrap(Driver driver, WebElement element) {
    return element instanceof SelenideElement ?
        (SelenideElement) element :
        (SelenideElement) Proxy.newProxyInstance(
            element.getClass().getClassLoader(), new Class<?>[]{SelenideElement.class},
            new SelenideElementProxy(new WebElementWrapper(driver, element)));
  }

  private final Driver driver;
  private final WebElement delegate;

  protected WebElementWrapper(Driver driver, WebElement delegate) {
    this.driver = driver;
    this.delegate = delegate;
  }

  @Override
  public WebElement getWebElement() {
    return delegate;
  }

  @Override
  public String getSearchCriteria() {
    return Describe.shortly(driver, delegate);
  }

  @Override
  public String toString() {
    return Describe.describe(driver(), delegate);
  }

  @Override
  public Driver driver() {
    return driver;
  }
}
