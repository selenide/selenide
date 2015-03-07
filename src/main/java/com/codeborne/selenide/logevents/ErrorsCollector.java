package com.codeborne.selenide.logevents;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAILED;
import static org.junit.Assert.fail;

public class ErrorsCollector implements LogEventListener {
  private final List<Throwable> errors = new ArrayList<Throwable>();

  @Override
  public void onEvent(LogEvent event) {
    if (event.getStatus() == FAILED) {
      errors.add(event.getError());
    }
  }
  
  public void clear() {
    errors.clear();
  }

  public void failIfErrors(String testName) {
    if (errors.size() == 1) {
      fail(errors.get(0).toString());
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
      fail(sb.toString());
    }
  }
}
