package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.Proxy;

import static com.codeborne.selenide.Configuration.pollingInterval;
import static com.codeborne.selenide.Configuration.timeout;

public class ElementLocatorProxy extends AbstractSelenideElement {
  public static SelenideElement wrap(ElementLocator elementLocator) {
    return (SelenideElement) Proxy.newProxyInstance(
        elementLocator.getClass().getClassLoader(),
        new Class<?>[]{SelenideElement.class}, new ElementLocatorProxy(elementLocator));
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
          Thread.sleep(pollingInterval);
        } catch (InterruptedException ignore) {
          Thread.currentThread().interrupt();
        }
      }
    } while (System.currentTimeMillis() - startTime < timeout);
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
