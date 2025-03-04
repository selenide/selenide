package org.selenide.videorecorder.core;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Holds the recorded videos per thread.
 * <p>
 * Can be used for retrieving the last recorded video after test execution.
 */
public class RecordedVideos {
  private static final Map<Long, Path> videos = new HashMap<>();

  public static void add(long threadId, Path videoFile) {
    videos.put(threadId, videoFile);
  }

  public static void remove(long threadId) {
    videos.remove(threadId);
  }

  public static Optional<Path> getRecordedVideo(long threadId) {
    return Optional.ofNullable(videos.get(threadId));
  }

  static void clear() {
    videos.clear();
  }
}
