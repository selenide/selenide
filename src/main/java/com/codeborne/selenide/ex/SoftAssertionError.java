package com.codeborne.selenide.ex;

import org.opentest4j.MultipleFailuresError;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SoftAssertionError extends MultipleFailuresError {
  public SoftAssertionError(String message, List<? extends Throwable> failures) {
    super(message, failures);
  }
}
