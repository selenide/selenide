package org.selenide.videorecorder.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;

import static com.codeborne.selenide.impl.ThreadNamer.named;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by Serhii Bryt
 * 07.05.2024 11:57
 */
public class VideoRecorder {
  private static final Logger log = LoggerFactory.getLogger(VideoRecorder.class);
  /**
   * Frames per seconds
   * 24fps is a standard for movies and TV shows,
   * but processing this amount of frames causes too high CPU usage.
   */
  private static final int DEFAULT_FPS = 12;

  /**
   * CRF operates on a scale from 0 (lossless) to 51 (lowest quality),
   * with lower values indicating higher quality and larger file sizes.
   */
  private static final int DEFAULT_CRF = 0;

  private final ScheduledExecutorService screenshooter = newScheduledThreadPool(1, named("video-recorder:screenshots:"));
  private final ScheduledExecutorService videoMerger = newScheduledThreadPool(1, named("video-recorder:stream:"));
  private final int fps;
  private final Queue<Screenshot> screenshots = new ConcurrentLinkedQueue<>();
  private final ScreenShooter screenShooterTask;
  private final VideoMerger videoMergerTask;

  public VideoRecorder() {
    this(DEFAULT_FPS);
  }

  public VideoRecorder(int framesPerSecond) {
    fps = framesPerSecond;
    screenShooterTask = new ScreenShooter(currentThread().getId(), screenshots);
    videoMergerTask = new VideoMerger(currentThread().getId(), fps, DEFAULT_CRF, screenshots);
  }

  public Optional<String> videoUrl() {
    return videoMergerTask.videoUrl();
  }

  public void start() {
    RecordedVideos.remove(currentThread().getId());
    screenshooter.scheduleAtFixedRate(screenShooterTask, 0, delayBetweenFramesMicros(), MICROSECONDS);
    videoMerger.scheduleWithFixedDelay(videoMergerTask, 0, 1, MILLISECONDS);
  }

  /**
   * FPS times per second
   */
  private long delayBetweenFramesMicros() {
    return SECONDS.toMicros(1) / fps;
  }

  public void stop() {
    log.debug("Stopping video recorder...");

    try {
      screenshooter.shutdown();
      stop(screenshooter, 2, "Screenshooter");
      screenShooterTask.finish();

      videoMerger.shutdown();
      stop(videoMerger, 20, "Video merger");

      log.info("Video recorded: {}", videoUrl().orElseThrow());
    }
    catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void stop(ScheduledExecutorService threadPool, int timeoutSeconds, String name) throws InterruptedException {
    if (!threadPool.awaitTermination(timeoutSeconds, SECONDS)) {
      log.warn("{} thread hasn't completed in {} seconds", name, timeoutSeconds);
    }
    else {
      log.debug("{} thread stopped within {} seconds", name, timeoutSeconds);
    }
  }
}
