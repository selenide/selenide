package com.codeborne.selenide.ex;

public class ErrorMessages {
  protected static String timeout(long timeoutMs) {
    if (timeoutMs < 1000) {
      return "\nTimeout: " + timeoutMs + " ms.";
    }
    if (timeoutMs % 1000 == 0) {
      return "\nTimeout: " + timeoutMs / 1000 + " s.";
    }
    return "\nTimeout: " + timeoutMs / 1000 + '.' + timeoutMs % 1000 + " s.";
  }
}
