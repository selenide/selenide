package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;

public class WebElementProxy extends AbstractSelenideElement {
  public static SelenideElement wrap(WebElement element) {
    return element instanceof SelenideElement ?
        (SelenideElement) element :
        (SelenideElement) Proxy.newProxyInstance(
            element.getClass().getClassLoader(), new Class<?>[]{SelenideElement.class}, new WebElementProxy(element));
  }

  private final WebElement delegate;

  WebElementProxy(WebElement delegate) {
    this.delegate = delegate;
  }


  @Override
  protected WebElement getDelegate() {
    return delegate;
  }

  @Override
  protected WebElement getActualDelegate() throws NoSuchElementException, IndexOutOfBoundsException {
    return getDelegate();
  }

  @Override
  protected String getSearchCriteria() {
    return Describe.shortly(delegate);
  }

  @Override
  public String toString() {
    return Describe.describe(delegate);
  }
}