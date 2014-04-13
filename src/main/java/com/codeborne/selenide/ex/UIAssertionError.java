package com.codeborne.selenide.ex;

import static com.codeborne.selenide.ex.ErrorMessages.*;

public class UIAssertionError extends AssertionError {
  protected UIAssertionError(String message) {
    super(message + screenshot() + jsErrors());
  }

  protected UIAssertionError(String message, long timeoutMs) {
    super(message + screenshot() + jsErrors() + timeout(timeoutMs));
  }

  protected UIAssertionError(String message, long timeoutMs, Exception cause) {
    super(message + screenshot() + jsErrors() + timeout(timeoutMs) + causedBy(cause));
  }
}
