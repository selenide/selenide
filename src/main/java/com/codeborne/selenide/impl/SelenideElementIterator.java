package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;

import java.util.Iterator;

public class SelenideElementIterator<T extends SelenideElement> implements Iterator<T> {
  protected final CollectionSource collection;
  private final Class<T> clazz;
  protected int index;

  public SelenideElementIterator(CollectionSource collection, Class<T> clazz) {
    this.collection = collection;
    this.clazz = clazz;
  }

  @Override
  public boolean hasNext() {
    return collection.getElements().size() > index;
  }

  @Override
  @SuppressWarnings("IteratorNextCanNotThrowNoSuchElementException")
  public T next() {
    return CollectionElement.wrap(clazz, collection, index++);
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("Cannot remove elements from web page");
  }
}
