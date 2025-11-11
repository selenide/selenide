package org.selenide.videorecorder.core;

import com.codeborne.selenide.impl.SelenideProperties;

/**
 * Inspired by Video Recorder from Sergey Pirogov
 *
 * These settings can be set only once, and only using these two means:
 * 1. via file "selenide.properties", or
 * 2. via system properties (has higher priority)
 *
 * @see <a href="https://github.com/SergeyPirogov/video-recorder-java/blob/master/core/src/main/java/com/automation/remarks/video/recorder/VideoConfiguration.java">VideoConfiguration</a>
 */
public class SelenideVideoConfiguration implements VideoConfiguration {
  private static final SelenideProperties properties = new SelenideProperties();

  @Override
  public boolean videoEnabled() {
    return properties.getBoolean("selenide.video.enabled", true);
  }

  @Override
  public String videoFolder() {
    return properties.getProperty("selenide.video.folder",
      System.getProperty("selenide.reportsFolder", "build/reports/tests"));
  }

  @Override
  public RecordingMode mode() {
    return RecordingMode.valueOf(properties.getProperty("selenide.video.mode", "ANNOTATED"));
  }

  @Override
  public VideoSaveMode saveMode() {
    return VideoSaveMode.valueOf(properties.getProperty("selenide.video.save.mode", "FAILED_ONLY"));
  }

  @Override
  public int fps() {
    return properties.getInt("selenide.video.fps", 24);
  }

  @Override
  public int crf() {
    return properties.getInt("selenide.video.crf", 0);
  }

  @Override
  public long videoProcessingTimeout() {
    return properties.getLong("selenide.video.processing.timeout", 300_000L);
  }

  @Override
  public boolean keepScreenshots() {
    return properties.getBoolean("selenide.video.keep-screenshots", false);
  }
}
