package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ElementIsNotClickableException extends UIAssertionError {
  public ElementIsNotClickableException(Driver driver, Throwable cause) {
    super(driver, "Element is not clickable", cause);
  }
}
