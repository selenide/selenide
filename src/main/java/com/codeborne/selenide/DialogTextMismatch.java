package com.codeborne.selenide;

public class DialogTextMismatch extends AssertionError {
  public DialogTextMismatch(String actualText, String expectedText) {
    super("\nActual: " + actualText +
        "\nExpected: " + expectedText);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + ' ' + getMessage();
  }
}
