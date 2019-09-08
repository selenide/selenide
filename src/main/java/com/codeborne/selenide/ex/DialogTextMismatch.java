package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

public class DialogTextMismatch extends UIAssertionError {
  public DialogTextMismatch(Driver driver, String actualText, String expectedText) {
    super(driver,
      "Dialog text mismatch" +
        "\nActual: " + actualText +
        "\nExpected: " + expectedText);
  }
}
