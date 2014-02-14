package com.codeborne.selenide.ex;

import static com.codeborne.selenide.ex.ErrorMessages.screenshot;
import static com.codeborne.selenide.ex.ErrorMessages.timeout;

public class UIAssertionError extends AssertionError {
  public UIAssertionError(String message) {
    super(message + screenshot());
  }

  public UIAssertionError(String message, long timeoutMs) {
    super(message + screenshot() + timeout(timeoutMs));
  }
}
