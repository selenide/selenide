package com.codeborne.selenide.impl;

import com.codeborne.selenide.ShouldableWebElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;

public class ShouldableWebElementProxy extends AbstractShouldableWebElementProxy {
  public static ShouldableWebElement wrap(WebElement element) {
    return element instanceof ShouldableWebElement ?
        (ShouldableWebElement) element :
        (ShouldableWebElement) Proxy.newProxyInstance(
            element.getClass().getClassLoader(), new Class<?>[]{ShouldableWebElement.class}, new ShouldableWebElementProxy(element));
  }

  private final WebElement delegate;

  ShouldableWebElementProxy(WebElement delegate) {
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
  public String toString() {
    return getClass().getSimpleName() + "{" + delegate + "}";
  }
}