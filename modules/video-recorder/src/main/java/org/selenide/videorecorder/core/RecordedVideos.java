package org.selenide.videorecorder.core;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Holds the recorded videos per thread.
 * <p>
 * Can be used for retrieving the last recorded video after test execution.
 */
public class RecordedVideos {
  private static final ThreadLocal<Path> videos = new ThreadLocal<>();

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public void add(Optional<Path> video) {
    video.ifPresentOrElse(
      file -> videos.set(file),
      () -> videos.remove());
  }

  @SuppressWarnings("OptionalOfNullableMisuse")
  public Optional<Path> getRecordedVideo() {
    return Optional.ofNullable(videos.get());
  }
}
