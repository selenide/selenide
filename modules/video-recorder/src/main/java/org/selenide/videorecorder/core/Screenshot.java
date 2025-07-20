package org.selenide.videorecorder.core;

import com.codeborne.selenide.impl.Lazy;

import static com.codeborne.selenide.impl.Lazy.lazyEvaluated;

class Screenshot {
  private static final Lazy<ClasspathResource> LAST_SCREEN = lazyEvaluated(() -> lastScreen());

  private final long timestamp;
  final ImageSource screenshot;

  Screenshot(long timestamp, ImageSource screenshot) {
    this.timestamp = timestamp;
    this.screenshot = screenshot;
  }

  boolean isEnd() {
    return false;
  }

  @Override
  public String toString() {
    return "%s(%s:%s)".formatted(getClass().getSimpleName(), timestamp, screenshot);
  }

  static Screenshot endMarker(long timestamp) {
    return new EndMarker(timestamp);
  }

  private static ClasspathResource lastScreen() {
    return new ClasspathResource("selenide-screen.png");
  }

  private static class EndMarker extends Screenshot {
    EndMarker(long timestamp) {
      super(timestamp, LAST_SCREEN.get());
    }

    @Override
    boolean isEnd() {
      return true;
    }
  }
}
