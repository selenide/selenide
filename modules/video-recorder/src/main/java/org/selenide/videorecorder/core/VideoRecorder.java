package org.selenide.videorecorder.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;

import static com.codeborne.selenide.impl.ThreadNamer.named;
import static java.lang.Integer.toHexString;
import static java.lang.System.nanoTime;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by Serhii Bryt
 * 07.05.2024 11:57
 */
public class VideoRecorder {
  private static final Logger log = LoggerFactory.getLogger(VideoRecorder.class);
  private static final VideoConfiguration config = new VideoConfiguration();

  private final ScheduledExecutorService screenshooter = newScheduledThreadPool(1, named("video-recorder:screenshots:"));
  private final ScheduledExecutorService videoMerger = newScheduledThreadPool(1, named("video-recorder:stream:"));
  private final int fps;
  private final Queue<Screenshot> screenshots = new ConcurrentLinkedQueue<>();
  private final ScreenShooter screenShooterTask;
  private final VideoMerger videoMergerTask;

  public VideoRecorder() {
    fps = config.fps();
    screenShooterTask = new ScreenShooter(currentThread().getId(), screenshots);
    videoMergerTask = new VideoMerger(currentThread().getId(), fps, config.crf(), screenshots);
  }

  public Optional<String> videoUrl() {
    return videoMergerTask.videoUrl();
  }

  public void start() {
    log.info("Starting screenshooter every {} nanoseconds to achieve fps {}", delayBetweenFramesNanos(), fps);
    RecordedVideos.remove(currentThread().getId());
    screenshooter.scheduleAtFixedRate(screenShooterTask, 0, delayBetweenFramesNanos(), NANOSECONDS);
    videoMerger.scheduleWithFixedDelay(videoMergerTask, 0, 1, MILLISECONDS);
  }

  /**
   * FPS times per second
   */
  private long delayBetweenFramesNanos() {
    return SECONDS.toNanos(1) / fps;
  }

  /**
   * Complete video processing and save the video file
   */
  public void finish() {
    log.debug("Stopping video recorder...");

    try {
      screenshooter.shutdown();
      stop("Screenshooter", screenshooter, config.videoProcessingTimeout());
      screenShooterTask.finish();

      videoMerger.shutdown();
      stop("Video merger", videoMerger, config.videoProcessingTimeout());
      videoMergerTask.finish();

      videoUrl().ifPresentOrElse(
        url -> log.info("Video recorded: {}", videoUrl().orElseThrow()),
        () -> log.info("Video not recorded."));
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  /**
   * Stop video processing and delete the video file
   */
  public void cancel() {
    screenShooterTask.cancel();
    screenshooter.shutdownNow();
    videoMergerTask.rollback();
    videoMerger.shutdownNow();
  }

  private void stop(String name, ScheduledExecutorService threadPool, long timeoutMs) throws InterruptedException {
    long start = nanoTime();
    if (!threadPool.awaitTermination(timeoutMs, MILLISECONDS)) {
      log.warn("{} thread hasn't completed in {} ms.", name, timeoutMs);
    }
    else {
      log.debug("{} thread stopped in {} ms.", name, NANOSECONDS.toMillis(nanoTime() - start));
    }
  }

  @Override
  public String toString() {
    return "%s{fps:%s, queueSize:%s}@%s".formatted(getClass().getSimpleName(), fps, screenshots.size(), toHexString(hashCode()));
  }
}
