package org.selenide.videorecorder.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds the recorded videos per thread.
 * <p>
 * Can be used for retrieving the last recorded video after test execution.
 */
public class RecordedVideos {
  private static final Logger log = LoggerFactory.getLogger(RecordedVideos.class);

  private static final Map<Long, Path> videos = new ConcurrentHashMap<>();

  public static void add(long threadId, Path videoFile) {
    log.debug("add video file {} in thread {} (exists: {})", videoFile, threadId, videoFile.toFile().exists());
    videos.put(threadId, videoFile);
  }

  public static void remove(long threadId) {
    Path videoFile = videos.remove(threadId);
    log.debug("removed video file {} in thread {}", videoFile, threadId);
  }

  public static Optional<Path> getRecordedVideo(long threadId) {
    Path result = videos.get(threadId);
    log.debug("get video file {} in thread {} (exists: {})", result, threadId, result != null && result.toFile().exists());
    return Optional.ofNullable(result);
  }

  static void clear() {
    log.debug("clear all video files: {}", videos);
    videos.clear();
  }
}
