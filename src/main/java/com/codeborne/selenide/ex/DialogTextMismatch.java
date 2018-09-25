package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

public class DialogTextMismatch extends UIAssertionError {
  public DialogTextMismatch(Driver driver, String actualText, String expectedText) {
    super(driver,
      "\nActual: " + actualText +
        "\nExpected: " + expectedText);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + ' ' + getMessage() + uiDetails();
  }
}
