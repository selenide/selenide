package org.selenide.videorecorder.core;

import com.codeborne.selenide.PropertiesReader;

/**
 * Inspired by Video Recorder from Sergey Pirogov
 *
 * These settings can be set only once, and only using these two means:
 * 1. via file "selenide.properties", or
 * 2. via system properties (has higher priority)
 *
 * @see <a href="https://github.com/SergeyPirogov/video-recorder-java/blob/master/core/src/main/java/com/automation/remarks/video/recorder/VideoConfiguration.java">VideoConfiguration</a>
 */
public class VideoConfiguration {
  private final PropertiesReader properties = new PropertiesReader("selenide.properties");

  /**
   * Is video recording enabled.
   * Use it to globally disable video recording in all tests (e.g. in some specific environments, build types etc.)
   *
   * @return true by default
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean videoEnabled() {
    return properties.getBoolean("selenide.video.enabled", true);
  }

  /**
   * The folder to save the videos to
   *
   * @return true by default
   */
  public String videoFolder() {
    return properties.getProperty("selenide.video.folder",
      System.getProperty("selenide.reportsFolder", "build/reports/tests"));
  }

  /**
   * Should the videos be recorded for all tests, or only for annotated with {@link Video}.
   * @return {@link RecordingMode#ANNOTATED} by default
   */
  public RecordingMode mode() {
    return RecordingMode.valueOf(properties.getProperty("selenide.video.mode", "ANNOTATED"));
  }

  /**
   * Should the videos be saved for all tests, or only for failed tests.
   * @return {@link VideoSaveMode#FAILED_ONLY} by default
   */
  public VideoSaveMode saveMode() {
    return VideoSaveMode.valueOf(properties.getProperty("selenide.video.save.mode", "FAILED_ONLY"));
  }

  /**
   * Frames per seconds
   * 24fps is a standard for movies and TV shows,
   * but processing this amount of frames causes too high CPU usage.
   */
  int fps() {
    return properties.getInt("selenide.video.fps", 12);
  }

  /**
   * CRF operates on a scale from 0 (lossless) to 51 (lowest quality),
   * with lower values indicating higher quality and larger file sizes.
   */
  int crf() {
    return properties.getInt("selenide.video.crf", 0);
  }

  /**
   * Timeout for processing a video (in milliseconds)
   *
   * Default value: 5 minutes (300000 ms)
   */
  long videoProcessingTimeout() {
    return properties.getLong("selenide.video.processing.timeout", 300_000L);
  }
}
