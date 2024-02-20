package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.codeborne.selenide.commands.Util.classOf;

@ParametersAreNonnullByDefault
public class SelenideElementIterator<T extends SelenideElement> implements Iterator<T> {
  protected final CollectionSource collection;
  private final Class<T> clazz;
  protected int index;

  @SafeVarargs
  public SelenideElementIterator(CollectionSource collection, T... clazz) {
    this.collection = collection;
    this.clazz = classOf(clazz);
  }

  @Override
  @CheckReturnValue
  public boolean hasNext() {
    return collection.getElements().size() > index;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public T next() {
    if (!hasNext()) {
      throw new NoSuchElementException("No next element present in collection %s at index %d".formatted(collection, index));
    }
    return CollectionElement.wrap(clazz, collection, index++);
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("Cannot remove elements from web page");
  }
}
