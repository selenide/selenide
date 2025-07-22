package org.selenide.videorecorder.core;

class Screenshot {
  private final long timestamp;
  final ImageSource screenshot;

  Screenshot(long timestamp, ImageSource screenshot) {
    this.timestamp = timestamp;
    this.screenshot = screenshot;
  }

  @Override
  public String toString() {
    return "%s(%s:%s)".formatted(getClass().getSimpleName(), timestamp, screenshot);
  }
}
