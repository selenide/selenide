package com.codeborne.selenide.impl;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Screenshot {
  private final String image;
  private final String source;

  public Screenshot(@Nullable String image, @Nullable String source) {
    this.image = image;
    this.source = source;
  }

  @CheckReturnValue
  @Nullable
  public String getImage() {
    return image;
  }

  @CheckReturnValue
  @Nullable
  public String getSource() {
    return source;
  }

  @CheckReturnValue
  @Nonnull
  public static Screenshot none() {
    return new Screenshot((String) null, null);
  }

  public String summary() {
    if (image != null && source != null) {
      return String.format("%nScreenshot: %s%nPage source: %s", image, source);
    }
    else if (source != null) {
      return String.format("%nPage source: %s", source);
    }
    else if (image != null) {
      return String.format("%nScreenshot: %s", image);
    }
    else {
      return String.format("%nScreenshot: null");
    }
  }

  @Override
  public String toString() {
    return summary();
  }
}
