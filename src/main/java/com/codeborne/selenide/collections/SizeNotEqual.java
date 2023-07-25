package com.codeborne.selenide.collections;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SizeNotEqual extends CollectionSizeCondition {
  public SizeNotEqual(int expectedSize) {
    super("<>", expectedSize);
  }

  @Override
  public String toString() {
    return String.format("size <> %s", expectedSize);
  }

  @CheckReturnValue
  @Override
  protected boolean apply(int size) {
    return size != expectedSize;
  }
}
