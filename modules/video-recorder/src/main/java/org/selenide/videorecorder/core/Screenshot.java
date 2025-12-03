package org.selenide.videorecorder.core;

class Screenshot {
  final long timestampNano;
  final ImageSource screenshot;

  Screenshot(long timestampNano, ImageSource screenshot) {
    this.timestampNano = timestampNano;
    this.screenshot = screenshot;
  }

  @Override
  public String toString() {
    return "%s(%s:%s)".formatted(getClass().getSimpleName(), timestampNano, screenshot);
  }
}
