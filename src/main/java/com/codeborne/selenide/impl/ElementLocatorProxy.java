package com.codeborne.selenide.impl;

import com.codeborne.selenide.DOM;
import com.codeborne.selenide.ShouldableWebElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.Proxy;

public class ElementLocatorProxy extends AbstractShouldableWebElementProxy {
  public static ShouldableWebElement wrap(ElementLocator elementLocator) {
    return (ShouldableWebElement) Proxy.newProxyInstance(
        elementLocator.getClass().getClassLoader(), new Class<?>[]{ShouldableWebElement.class}, new ElementLocatorProxy(elementLocator));
  }

  private final ElementLocator elementLocator;

  ElementLocatorProxy(ElementLocator elementLocator) {
    this.elementLocator = elementLocator;
  }

  @Override
  protected WebElement getDelegate() {
    long startTime = System.currentTimeMillis();
    NoSuchElementException exception;
    do {
      try {
        return elementLocator.findElement();
      } catch (NoSuchElementException e) {
        exception = e;
        try {
          Thread.sleep(100);
        } catch (InterruptedException ignore) {
        }
      }
    } while (System.currentTimeMillis() - startTime < DOM.defaultWaitingTimeout);
    throw exception;
  }

  @Override
  protected WebElement getActualDelegate() throws NoSuchElementException, IndexOutOfBoundsException {
    return elementLocator.findElement();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" + elementLocator + "}";
  }
}
