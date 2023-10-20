package com.codeborne.selenide.impl;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.util.List;

/**
 * Override Selenium's DefaultFieldDecorator to allow page object fields of type {@code List<T>} where {@code T extends WebElement}.
 *
 * The problem is that DefaultFieldDecorator cannot detect the actual type of `T` (method isDecoratableList returns false),
 * but Selenide can.
 */
class SelenideFieldDecorator extends DefaultFieldDecorator {
  SelenideFieldDecorator(ElementLocatorFactory factory) {
    super(factory);
  }

  @Override
  protected List<WebElement> proxyForListLocator(ClassLoader loader, ElementLocator locator) {
    return super.proxyForListLocator(loader, locator);
  }
}
