package com.codeborne.selenide.ex;

public class DialogTextMismatch extends UIAssertionError {
  public DialogTextMismatch(String actualText, String expectedText) {
    super("\nActual: " + actualText +
        "\nExpected: " + expectedText);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + ' ' + getMessage();
  }
}
