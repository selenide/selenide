package org.selenide.videorecorder.core;

public interface VideoConfiguration {
  /**
   * Is video recording enabled.
   * Use it to globally disable video recording in all tests (e.g. in some specific environments, build types etc.)
   *
   * @return true by default
   */
  boolean videoEnabled();

  /**
   * The folder to save the videos to
   *
   * @return "build/reports/tests" by default
   */
  String videoFolder();

  /**
   * Should the videos be recorded for all tests, or only for annotated with {@link Video}.
   * @return {@link RecordingMode#ANNOTATED} by default
   */
  RecordingMode mode();

  /**
   * Should the videos be saved for all tests, or only for failed tests.
   * @return {@link VideoSaveMode#FAILED_ONLY} by default
   */
  VideoSaveMode saveMode();

  /**
   * Frames per seconds
   * 24fps is a standard for movies and TV shows,
   * but processing this amount of frames causes too high CPU usage.
   */
  int fps();

  /**
   * CRF operates on a scale from 0 (lossless) to 51 (lowest quality),
   * with lower values indicating higher quality and larger file sizes.
   */
  int crf();

  /**
   * Timeout for processing a video (in milliseconds)
   *
   * Default value: 5 minutes (300000 ms)
   */
  long videoProcessingTimeout();

  /**
   * Is it needed to keep the taken screenshots after the video has been created from them.
   *
   * Default value: false
   */
  boolean keepScreenshots();
}
