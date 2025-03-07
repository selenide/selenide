package com.codeborne.selenide.collections;

public class ListSize extends CollectionSizeCondition {

  public ListSize(int expectedSize) {
    super("=", expectedSize);
  }

  @Override
  public String toString() {
    return String.format("size(%s)", expectedSize);
  }

  @Override
  protected boolean apply(int size) {
    return size == expectedSize;
  }
}
