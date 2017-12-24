package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;

import java.util.Iterator;
import java.util.List;

public class SelenideElementIterator implements Iterator<SelenideElement> {
  protected final WebElementsCollection collection;
  protected List<WebElement> actualElements;
  protected int index;

  public SelenideElementIterator(WebElementsCollection collection) {
    this.collection = collection;
    this.actualElements = collection.getActualElements();
  }

  @Override
  public boolean hasNext() {
    return actualElements.size() > index || (actualElements = collection.getActualElements()).size() > index;
  }

  @Override
  public SelenideElement next() {
    return CollectionElement.wrap(collection, actualElements, index++);
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("Cannot remove elements from web page");
  }
}
