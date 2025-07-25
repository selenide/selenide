package org.selenide.videorecorder.core;

import com.codeborne.selenide.impl.AttachmentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import static com.codeborne.selenide.impl.FileHelper.deleteFolder;
import static com.codeborne.selenide.impl.FileHelper.ensureFolderExists;
import static com.codeborne.selenide.impl.Plugins.inject;
import static com.codeborne.selenide.impl.ThreadNamer.named;
import static java.lang.Integer.toHexString;
import static java.lang.System.currentTimeMillis;
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
  private static final AtomicLong videoCounter = new AtomicLong(0);
  private final AttachmentHandler attachmentHandler = inject();

  private final ScheduledExecutorService screenshooter = newScheduledThreadPool(1, named("video-recorder:screenshots:"));
  private final int fps;
  private final Queue<Screenshot> screenshots = new ConcurrentLinkedQueue<>();
  private final File screenshotsFolder;
  private final ScreenShooter screenShooterTask;
  private final VideoMerger videoMerger;

  public VideoRecorder() {
    fps = config.fps();
    String videoId = "%s.%s".formatted(currentTimeMillis(), videoCounter.getAndIncrement());
    screenshotsFolder = createScreenshotsFolder(videoId);
    screenShooterTask = new ScreenShooter(currentThread().getId(), screenshotsFolder, screenshots);
    videoMerger = new VideoMerger(currentThread().getId(), videoId, config, screenshotsFolder, screenshots);
  }

  private static File createScreenshotsFolder(String videoId) {
    File screenshotsFolder = new File(config.videoFolder(), "video.%s.screenshots".formatted(videoId));
    ensureFolderExists(screenshotsFolder);
    if (!config.keepScreenshots()) {
      screenshotsFolder.deleteOnExit();
    }
    return screenshotsFolder;
  }

  public String videoUrl() {
    return videoMerger.videoUrl();
  }

  public void start() {
    log.info("Starting screenshooter every {} nanoseconds to achieve fps {}", delayBetweenFramesNanos(), fps);
    startScreenShooter();
  }

  private void startScreenShooter() {
    log.debug("Start screen shooter x {} {}", delayBetweenFramesNanos(), NANOSECONDS);
    screenshooter.scheduleAtFixedRate(screenShooterTask, 0, delayBetweenFramesNanos(), NANOSECONDS);
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
      screenShooterTask.cancel();
      screenshooter.shutdown();
      stop("Screenshooter", screenshooter, 2000);
      screenshooter.shutdownNow();

      videoMerger.finish();

      log.info("Video recorded: {}", videoUrl());
      attachmentHandler.attach(videoMerger.videoFile().toFile());

      if (!config.keepScreenshots()) {
        deleteFolder(screenshotsFolder);
      }
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
    videoMerger.rollback();
    videoMerger.cancel();
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
