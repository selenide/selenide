package com.codeborne.selenide.collections;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ListSize extends CollectionSizeCondition {

  public ListSize(int expectedSize) {
    super("=", expectedSize);
  }

  @CheckReturnValue
  @Override
  public String toString() {
    return String.format("size(%s)", expectedSize);
  }

  @Override
  protected boolean apply(int size) {
    return size == expectedSize;
  }
}
