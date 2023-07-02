package com.codeborne.selenide.collections;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SizeGreaterThan extends CollectionSizeCondition {
  public SizeGreaterThan(int expectedSize) {
    super(">", expectedSize);
  }

  @Override
  public String toString() {
    return String.format("size > %s", expectedSize);
  }

  @CheckReturnValue
  @Override
  protected boolean apply(int size) {
    return size > expectedSize;
  }
}
