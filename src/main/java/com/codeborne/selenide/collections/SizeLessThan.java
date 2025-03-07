package com.codeborne.selenide.collections;

public class SizeLessThan extends CollectionSizeCondition {
  public SizeLessThan(int expectedSize) {
    super("<", expectedSize);
  }

  @Override
  public String toString() {
    return String.format("size < %s", expectedSize);
  }

  @Override
  protected boolean apply(int size) {
    return size < expectedSize;
  }
}
