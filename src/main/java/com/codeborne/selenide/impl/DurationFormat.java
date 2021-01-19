package com.codeborne.selenide.impl;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;

@ParametersAreNonnullByDefault
public class DurationFormat {
  @CheckReturnValue
  public String format(Duration duration) {
    return format(duration.toMillis());
  }

  @CheckReturnValue
  public String format(long milliseconds) {
    if (milliseconds < 1000) {
      return String.format("%d ms.", milliseconds);
    }
    if (milliseconds % 1000 == 0) {
      return String.format("%d s.", milliseconds / 1000);
    }

    return String.format("%.3f s.", milliseconds / 1000.0);
  }
}
