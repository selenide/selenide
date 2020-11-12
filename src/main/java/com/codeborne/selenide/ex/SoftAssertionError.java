package com.codeborne.selenide.ex;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SoftAssertionError extends AssertionError {
  public SoftAssertionError(String message) {
    super(message);
  }
}
