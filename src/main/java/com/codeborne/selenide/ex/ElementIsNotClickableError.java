package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ElementIsNotClickableError extends UIAssertionError {
  public ElementIsNotClickableError(Driver driver, String elementDescription, Throwable cause) {
    super(driver, "Element is not clickable: " + elementDescription, cause);
  }
}
