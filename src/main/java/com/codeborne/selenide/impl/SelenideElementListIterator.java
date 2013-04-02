package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;

import java.util.ListIterator;

public class SelenideElementListIterator extends SelenideElementIterator implements ListIterator<SelenideElement> {
  private final ListIterator<WebElement> webElementListIterator;

  public SelenideElementListIterator(ListIterator<WebElement> webElementListIterator) {
    super(webElementListIterator);
    this.webElementListIterator = webElementListIterator;
  }

  @Override
  public boolean hasPrevious() {
    return webElementListIterator.hasPrevious();
  }

  @Override
  public SelenideElement previous() {
    return WebElementProxy.wrap(webElementListIterator.previous());
  }

  @Override
  public int nextIndex() {
    return webElementListIterator.nextIndex();
  }

  @Override
  public int previousIndex() {
    return webElementListIterator.previousIndex();
  }

  @Override
  public void set(SelenideElement selenideElement) {
    throw new UnsupportedOperationException("Cannot set elements to web page");
  }

  @Override
  public void add(SelenideElement selenideElement) {
    throw new UnsupportedOperationException("Cannot add elements to web page");
  }
}
