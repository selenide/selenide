package com.codeborne.selenide.logevents;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.ex.SoftAssertionError;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAIL;

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

  public void clear() {
    errors.clear();
  }

  public void failIfErrors(String testName) {
    if (errors.size() == 1) {
      throw new SoftAssertionError(errors.get(0).toString());
    }
    if (!errors.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      sb.append("Test ").append(testName).append(" failed.\n");
      sb.append(errors.size()).append(" checks failed\n");

      int i = 0;
      for (Throwable error : errors) {
        sb.append("\nFAIL #").append(++i).append(": ");
        sb.append(error).append('\n');
      }
      throw new SoftAssertionError(sb.toString());
    }
  }

  public static void validateAssertionMode(Config config) {
    if (config.assertionMode() == SOFT) {
      if (!SelenideLogger.hasListener(LISTENER_SOFT_ASSERT)) {
        throw new IllegalStateException("Using soft asserts, but without @SoftAsserts annotation");
      }
    }
  }
}
