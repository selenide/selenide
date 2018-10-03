package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SelenideElementIterator implements Iterator<SelenideElement> {
  protected final WebElementsCollection collection;
  protected int index;

  public SelenideElementIterator(WebElementsCollection collection) {
    this.collection = collection;
  }

  @Override
  public boolean hasNext() {
    return collection.getElements().size() > index;
  }

  @Override
  public SelenideElement next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    return CollectionElement.wrap(collection, index++);
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("Cannot remove elements from web page");
  }
}
