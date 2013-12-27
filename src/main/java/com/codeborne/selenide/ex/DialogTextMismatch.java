package com.codeborne.selenide.ex;

import static com.codeborne.selenide.ex.ErrorMessages.screenshot;

public class DialogTextMismatch extends AssertionError {
  public DialogTextMismatch(String actualText, String expectedText) {
    super("\nActual: " + actualText +
        "\nExpected: " + expectedText +
        screenshot());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + ' ' + getMessage();
  }
}
