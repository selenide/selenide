package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

public class ElementIsNotClickableException extends UIAssertionError {
  public ElementIsNotClickableException(Driver driver, Throwable cause) {
    super(driver, "Element is not clickable", cause);
  }
}
