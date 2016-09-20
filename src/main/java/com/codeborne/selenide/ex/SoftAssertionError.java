package com.codeborne.selenide.ex;

public class SoftAssertionError extends AssertionError {
  public SoftAssertionError(String message) {
    super(message);
  }
}
