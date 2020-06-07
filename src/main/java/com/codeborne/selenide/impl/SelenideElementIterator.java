package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;
import java.util.NoSuchElementException;

@ParametersAreNonnullByDefault
public class SelenideElementIterator implements Iterator<SelenideElement> {
  protected final WebElementsCollection collection;
  protected int index;

  public SelenideElementIterator(WebElementsCollection collection) {
    this.collection = collection;
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
