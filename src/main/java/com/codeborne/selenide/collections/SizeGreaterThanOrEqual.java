package com.codeborne.selenide.collections;

public class SizeGreaterThanOrEqual extends CollectionSizeCondition {
  public SizeGreaterThanOrEqual(int expectedSize) {
    super(">=", expectedSize);
  }

  @Override
  public String toString() {
    return String.format("size >= %s", expectedSize);
  }

  @Override
  protected boolean apply(int size) {
    return size >= expectedSize;
  }
}
