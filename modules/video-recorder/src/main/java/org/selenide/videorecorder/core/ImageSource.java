package org.selenide.videorecorder.core;

import java.io.File;

public record ImageSource(File file) {
  @Override
  public String toString() {
    return file.toString();
  }
}
