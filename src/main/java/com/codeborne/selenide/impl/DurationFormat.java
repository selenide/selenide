package com.codeborne.selenide.impl;

import java.time.Duration;

import static java.util.Locale.ROOT;

public class DurationFormat {
  public String format(Duration duration) {
    return format(duration.toMillis());
  }

  public String format(long milliseconds) {
    if (milliseconds < 1000) {
      return String.format("%d ms.", milliseconds);
    }
    if (milliseconds % 1000 == 0) {
      return String.format("%d s.", milliseconds / 1000);
    }

    return String.format(ROOT, "%.3f s.", milliseconds / 1000.0);
  }
}
