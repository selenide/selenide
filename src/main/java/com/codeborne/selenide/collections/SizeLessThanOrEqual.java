package com.codeborne.selenide.collections;

public class SizeLessThanOrEqual extends CollectionSizeCondition {
  public SizeLessThanOrEqual(int expectedSize) {
    super("<=", expectedSize);
  }

  @Override
  public String toString() {
    return String.format("size <= %s", expectedSize);
  }

  @Override
  protected boolean apply(int size) {
    return size <= expectedSize;
  }
}
