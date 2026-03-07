package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.TimeoutException;

import static com.codeborne.selenide.impl.CommandGuard.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommandGuardTest {
  @Test
  void commandCompletesInTime() {
    String result = CommandGuard.executeWithTimeout(100, () -> "File loaded");
    assertThat(result).isEqualTo("File loaded");
  }

  @Test
  void commandIsRunningLongerThanTimeout() {
    assertThatThrownBy(() -> CommandGuard.executeWithTimeout(100, () -> loadFileSlowly(200)))
      .isInstanceOf(TimeoutException.class)
      .hasMessageStartingWith("Interrupted while loading the file")
      .cause()
      .isInstanceOf(InterruptedException.class);
  }

  private String loadFileSlowly(long durationMs) {
    try {
      sleep(durationMs);
    }
    catch (InterruptedException e) {
      throw new TimeoutException("Interrupted while loading the file", e);
    }
    return "File loaded";
  }
}
