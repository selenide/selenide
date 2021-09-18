package com.codeborne.selenide.logevents;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.ex.SoftAssertionError;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAIL;
import static java.lang.System.lineSeparator;
import static java.util.Collections.unmodifiableList;

@ParametersAreNonnullByDefault
public class ErrorsCollector implements LogEventListener {
  public static final String LISTENER_SOFT_ASSERT = "softAssert";

  private final List<Throwable> errors = new ArrayList<>();

  @Override
  public void afterEvent(LogEvent event) {
    if (event.getStatus() == FAIL) {
      errors.add(event.getError());
    }
  }

  @Override
  public void beforeEvent(LogEvent currentLog) {
    // ignore
  }

  void clear() {
    errors.clear();
  }

  List<Throwable> getErrors() {
    return unmodifiableList(errors);
  }

  /**
   * 1. Clears all collected errors, and
   * 2. throws SoftAssertionError if there were some errors
   *
   * @param testName any string, usually name of current test
   */
  public void failIfErrors(String testName) {
    List<Throwable> errors = new ArrayList<>(this.errors);
    this.errors.clear();

    if (errors.size() == 1) {
      throw new SoftAssertionError(errors.get(0).toString());
    }
    if (!errors.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      sb.append("Test ").append(testName).append(" failed.").append(lineSeparator());
      sb.append(errors.size()).append(" checks failed").append(lineSeparator());

      int i = 0;
      for (Throwable error : errors) {
        sb.append(lineSeparator()).append("FAIL #").append(++i).append(": ");
        sb.append(error).append(lineSeparator());
      }
      throw new SoftAssertionError(sb.toString());
    }
  }

  public static void validateAssertionMode(Config config) {
    if (config.assertionMode() == SOFT) {
      if (!SelenideLogger.hasListener(LISTENER_SOFT_ASSERT)) {
        throw new IllegalStateException("You must configure you classes using JUnit4/JUnit5/TestNG " +
          "mechanism as documented in https://github.com/selenide/selenide/wiki/SoftAssertions");
      }
    }
  }
}
