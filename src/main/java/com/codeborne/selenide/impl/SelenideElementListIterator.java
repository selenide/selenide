package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ListIterator;

@ParametersAreNonnullByDefault
public class SelenideElementListIterator extends SelenideElementIterator implements ListIterator<SelenideElement> {
  public SelenideElementListIterator(WebElementsCollection collection, int index) {
    super(collection);
    this.index = index;
  }

  @Override
  @CheckReturnValue
  public boolean hasPrevious() {
    return index > 0;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public SelenideElement previous() {
    return CollectionElement.wrap(collection, --index);
  }

  @Override
  @CheckReturnValue
  public int nextIndex() {
    return index + 1;
  }

  @Override
  @CheckReturnValue
  public int previousIndex() {
    return index - 1;
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
