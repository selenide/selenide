package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;

public class WebElementWrapper extends WebElementSource {
  public static SelenideElement wrap(WebElement element) {
    return element instanceof SelenideElement ?
        (SelenideElement) element :
        (SelenideElement) Proxy.newProxyInstance(
            element.getClass().getClassLoader(), new Class<?>[]{SelenideElement.class},
            new SelenideElementProxy(new WebElementWrapper(element)));
  }

  private final WebElement delegate;

  protected WebElementWrapper(WebElement delegate) {
    this.delegate = delegate;
  }

  @Override
  public WebElement getWebElement() {
    return delegate;
  }

  @Override
  public String getSearchCriteria() {
    return Describe.shortly(delegate);
  }

  @Override
  public String toString() {
    return Describe.describe(delegate);
  }
}
