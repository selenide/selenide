package com.codeborne.selenide.ex;

import org.opentest4j.MultipleFailuresError;

import java.util.List;

public class SoftAssertionError extends MultipleFailuresError {
  public SoftAssertionError(String message, List<? extends Throwable> failures, boolean fullStacktraces) {
    super(message, failures);
    if (fullStacktraces) {
      for (Throwable failure : failures) {
        addSuppressed(failure);
      }
    }
  }
}
