package com.codeborne.selenide.ex;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DialogTextMismatch extends UIAssertionError {
  public DialogTextMismatch(String expectedText, String actualText) {
    super(
      String.format("Dialog text mismatch" +
        "%nActual: %s" +
        "%nExpected: %s", actualText, expectedText),
      expectedText, actualText);
  }
}
