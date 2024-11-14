package org.selenide.videorecorder.core;

import org.openqa.selenium.Dimension;

import java.nio.file.Path;

class Screenshot {
  private static final byte[] END_MARKER = {};

  final long timestamp;
  final Dimension window;
  final Path videoFile;
  final byte[] screenshot;

  Screenshot(long timestamp, Dimension window, Path videoFile, byte[] screenshot) {
    this.timestamp = timestamp;
    this.window = window;
    this.videoFile = videoFile;
    this.screenshot = screenshot;
  }

  boolean isEnd() {
    return false;
  }

  static Screenshot endMarker(long timestamp) {
    return new EndMarker(timestamp);
  }

  @Override
  public String toString() {
    return "%s(%s)".formatted(getClass().getSimpleName(), timestamp);
  }

  private static class EndMarker extends Screenshot {
    EndMarker(long timestamp) {
      super(timestamp, new Dimension(0, 0), Path.of("."), END_MARKER);
    }

    @Override
    boolean isEnd() {
      return true;
    }
  }
}
