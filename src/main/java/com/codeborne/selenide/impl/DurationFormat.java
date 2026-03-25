package com.codeborne.selenide.impl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;

import static java.util.Locale.ROOT;

public class DurationFormat {
  private static final DecimalFormat SECONDS = new DecimalFormat("0.###", DecimalFormatSymbols.getInstance(ROOT));

  public String format(Duration duration) {
    return formatDuration(duration);
  }

  public String format(long milliseconds) {
    return formatDuration(milliseconds);
  }

  public static String formatDuration(Duration duration) {
    return formatDuration(duration.toMillis());
  }
  public static String formatDuration(long milliseconds) {
    if (milliseconds < 1000) {
      return String.format("%dms", milliseconds);
    }
    if (milliseconds % 1000 == 0) {
      return String.format("%ds", milliseconds / 1000);
    }

    return SECONDS.format(milliseconds / 1000.0) + "s";
  }
}
