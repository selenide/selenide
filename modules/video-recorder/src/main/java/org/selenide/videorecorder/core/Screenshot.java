package org.selenide.videorecorder.core;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.impl.Lazy;
import org.openqa.selenium.Dimension;

import static com.codeborne.selenide.impl.Lazy.lazyEvaluated;

class Screenshot {
  private static final Lazy<ImageSource> LAST_SCREEN = lazyEvaluated(() -> lastScreen());

  final long timestamp;
  final Dimension window;
  final Config config;
  final ImageSource screenshot;

  Screenshot(long timestamp, Dimension window, Config config, ImageSource screenshot) {
    this.timestamp = timestamp;
    this.window = window;
    this.config = config;
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

  private static ImageSource lastScreen() {
    return new ClasspathResource("/selenide-screen.png");
  }

  private static class EndMarker extends Screenshot {
    EndMarker(long timestamp) {
      super(timestamp, new Dimension(0, 0), new SelenideConfig(), LAST_SCREEN.get());
    }

    @Override
    boolean isEnd() {
      return true;
    }
  }
}
