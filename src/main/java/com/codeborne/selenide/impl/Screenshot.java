package com.codeborne.selenide.impl;

import org.jspecify.annotations.Nullable;

import java.io.File;

public class Screenshot {
  @Nullable
  private final File imageFile;
  @Nullable
  private final String image;
  @Nullable
  private final String source;

  public Screenshot(@Nullable File imageFile, @Nullable String imageUrl, @Nullable String source) {
    this.imageFile = imageFile;
    this.image = imageUrl;
    this.source = source;
  }

  @Nullable
  File getImageFile() {
    return imageFile;
  }

  @Nullable
  public String getImage() {
    return image;
  }

  @Nullable
  public String getSource() {
    return source;
  }

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
