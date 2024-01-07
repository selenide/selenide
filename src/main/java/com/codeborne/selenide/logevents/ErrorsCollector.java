package com.codeborne.selenide.logevents;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.ex.SoftAssertionError;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAIL;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

@ParametersAreNonnullByDefault
public class ErrorsCollector implements LogEventListener {
  private static final AtomicLong counter = new AtomicLong(0);
  public static final String LISTENER_SOFT_ASSERT = "softAssert";

  private final String id;
  private final List<Throwable> errors;

  public ErrorsCollector() {
    this(String.valueOf(counter.incrementAndGet()), emptyList());
  }

  private ErrorsCollector(String id, List<Throwable> errors) {
    this.id = id;
    this.errors = new ArrayList<>(errors);
  }

  /**
   * Make a copy of this collector.
   * Used to take over errors collected in "before all" to each test.
   * @return A new collector that contains all errors collected by this collector
   */
  public ErrorsCollector copy() {
    return new ErrorsCollector(id + "." + counter.incrementAndGet(), errors);
  }

  protected boolean isEnabled() {
    return true;
  }

  @Override
  public void afterEvent(LogEvent event) {
    if (isEnabled() && event.getStatus() == FAIL) {
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
   * 2. returns SoftAssertionError if there were some errors
   *
   * @param testName any string, usually name of current test
   */
  @Nullable
  public AssertionError cleanAndGetAssertionError(String testName, @Nullable Throwable testFailure, boolean fullStacktraces) {
    List<Throwable> errors = new ArrayList<>(this.errors);
    if (testFailure != null) {
      errors.add(testFailure);
    }
    this.errors.clear();

    if (errors.size() == 1 && errors.get(0) instanceof AssertionError assertionError) {
      return assertionError;
    }
    if (!errors.isEmpty()) {
      String message = String.format("Test %s failed", testName);
      return new SoftAssertionError(message, errors, fullStacktraces);
    }
    return null;
  }

  public void cleanAndThrowAssertionError(String testName, @Nullable Throwable testFailure, boolean fullStacktraces) {
    AssertionError error = cleanAndGetAssertionError(testName, testFailure, fullStacktraces);
    if (error != null) {
      throw error;
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

  @Override
  public String toString() {
    return "%s #%s [%d errors]".formatted(getClass().getSimpleName(), id, errors.size());
  }
}
