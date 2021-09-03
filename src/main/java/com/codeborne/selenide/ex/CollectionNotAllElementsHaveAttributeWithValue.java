package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

import javax.annotation.Nullable;

public class CollectionNotAllElementsHaveAttributeWithValue extends UIAssertionError {

  public CollectionNotAllElementsHaveAttributeWithValue(Driver driver, String message, @Nullable Throwable cause) {
    super(driver, message, cause);
  }
}
