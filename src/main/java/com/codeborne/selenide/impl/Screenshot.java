package com.codeborne.selenide.impl;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

public class Screenshot {
  private final File imageFile;
  private final String image;
  private final String source;

  public Screenshot(@Nullable File imageFile, @Nullable String imageUrl, @Nullable String source) {
    this.imageFile = imageFile;
    this.image = imageUrl;
    this.source = source;
  }

  @CheckReturnValue
  @Nullable
  File getImageFile() {
    return imageFile;
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
    return new Screenshot(null, null, null);
  }

  public boolean isPresent() {
    return image != null || source != null;
  }

  public String summary() {
    if (image != null && source != null) {
      return String.format("Screenshot: %s%nPage source: %s", image, source);
    }
    else if (source != null) {
      return String.format("Page source: %s", source);
    }
    else if (image != null) {
      return String.format("Screenshot: %s", image);
    }
    else {
      return "";
    }
  }

  @Override
  public String toString() {
    return summary();
  }
}
