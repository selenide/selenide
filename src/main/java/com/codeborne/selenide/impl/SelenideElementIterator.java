package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;

import java.util.Iterator;

public class SelenideElementIterator implements Iterator<SelenideElement> {
  private final Iterator<WebElement> webElementIterator;

  public SelenideElementIterator(Iterator<WebElement> webElementIterator) {
    this.webElementIterator = webElementIterator;
  }

  @Override
  public boolean hasNext() {
    return webElementIterator.hasNext();
  }

  @Override
  public SelenideElement next() {
    return WebElementProxy.wrap(webElementIterator.next());
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("Cannot remove elements from web page");
  }
}
