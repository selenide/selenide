package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DialogTextMismatch extends UIAssertionError {
  public DialogTextMismatch(Driver driver, String actualText, String expectedText) {
    super(driver,
      String.format("Dialog text mismatch" +
        "%nActual: %s" +
        "%nExpected: %s", actualText, expectedText));
  }
}
