package org.selenide.videorecorder.core;

import com.codeborne.selenide.PropertiesReader;

/**
 * Inspired by Video Recorder from Sergey Pirogov
 *
 * @see <a href="https://github.com/SergeyPirogov/video-recorder-java/blob/master/core/src/main/java/com/automation/remarks/video/recorder/VideoConfiguration.java">VideoConfiguration</a>
 */
public class VideoConfiguration {
  private final PropertiesReader properties = new PropertiesReader("selenide.properties");

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean videoEnabled() {
    return properties.getBoolean("selenide.video.enabled", true);
  }

  public RecordingMode mode() {
    return RecordingMode.valueOf(properties.getProperty("selenide.video.mode", "ANNOTATED"));
  }

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
   */
  long videoProcessingTimeout() {
    return properties.getLong("selenide.video.processing.timeout", 20_000L);
  }
}
