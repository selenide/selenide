package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Cleanup;

public class ElementIsNotClickableException extends UIAssertionError {
  public ElementIsNotClickableException(Driver driver, Throwable cause) {
    super(driver, cause);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + Cleanup.of.webdriverExceptionMessage(getCause()) + uiDetails();
  }
}
