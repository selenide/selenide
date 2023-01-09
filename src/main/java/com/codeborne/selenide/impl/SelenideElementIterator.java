package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;
import java.util.NoSuchElementException;

@ParametersAreNonnullByDefault
public class SelenideElementIterator implements Iterator<SelenideElement> {
  protected final CollectionSource collection;
  protected int index;

  public SelenideElementIterator(CollectionSource collection) {
    this(collection, 0);
  }

  protected SelenideElementIterator(CollectionSource collection, int index) {
    this.collection = collection;
    this.index = index;
  }

  @Override
  @CheckReturnValue
  public boolean hasNext() {
    return collection.getElements().size() > index;
  }

  @Override
  @CheckReturnValue
  @Nonnull
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
