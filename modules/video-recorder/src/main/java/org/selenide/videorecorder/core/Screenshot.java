package org.selenide.videorecorder.core;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.impl.Lazy;
import org.openqa.selenium.Dimension;

import java.io.IOException;

import static com.codeborne.selenide.impl.Lazy.lazyEvaluated;
import static org.apache.commons.io.IOUtils.resourceToByteArray;

class Screenshot {
  private static final Lazy<byte[]> LAST_SCREEN = lazyEvaluated(() -> lastScreen());

  final long timestamp;
  final Dimension window;
  final Config config;
  final byte[] screenshot;

  Screenshot(long timestamp, Dimension window, Config config, byte[] screenshot) {
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
    return "%s(%s)".formatted(getClass().getSimpleName(), timestamp);
  }

  static Screenshot endMarker(long timestamp) {
    return new EndMarker(timestamp);
  }

  private static byte[] lastScreen() {
    try {
      return resourceToByteArray("/selenide-screen.png");
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
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
