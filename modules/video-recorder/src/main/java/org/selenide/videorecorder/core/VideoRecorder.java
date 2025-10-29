package org.selenide.videorecorder.core;

import com.codeborne.selenide.impl.AttachmentHandler;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;

import static com.codeborne.selenide.impl.FileHelper.deleteFolder;
import static com.codeborne.selenide.impl.FileHelper.ensureFolderExists;
import static com.codeborne.selenide.impl.Plugins.inject;
import static com.codeborne.selenide.impl.ThreadNamer.named;
import static java.lang.Integer.toHexString;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.Executors.newScheduledThreadPool;
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

  private static final ScheduledExecutorService screenshooter = newScheduledThreadPool(100, named("video-recorder:screenshots:"));
  private final String videoId;
  private final int fps;
  private final Queue<Screenshot> screenshots = new ConcurrentLinkedQueue<>();
  private final File screenshotsFolder;
  @Nullable
  private ScheduledFuture<?> screenShooter;
  private final VideoMerger videoMerger;

  public VideoRecorder() {
    fps = config.fps();
    videoId = "%s.%s".formatted(currentTimeMillis(), videoCounter.getAndIncrement());
    screenshotsFolder = createScreenshotsFolder(videoId);
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

  public String videoId() {
    return videoId;
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
    ScreenShooter task = new ScreenShooter(currentThread().getId(), screenshotsFolder, screenshots);
    screenShooter = screenshooter.scheduleAtFixedRate(task, 0, delayBetweenFramesNanos(), NANOSECONDS);
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

    if (screenShooter != null) {
      screenShooter.cancel(true);
    }
    videoMerger.finish();

    log.info("Video recorded: {}", videoUrl());
    attachmentHandler.attach(videoMerger.videoFile().toFile());

    if (!config.keepScreenshots()) {
      deleteFolder(screenshotsFolder);
    }
  }

  /**
   * Stop video processing and delete the video file
   */
  public void cancel() {
    if (screenShooter != null) {
      screenShooter.cancel(true);
    }
    videoMerger.rollback();
    videoMerger.cancel();
  }

  @Override
  public String toString() {
    return "%s{fps:%s, queueSize:%s}@%s".formatted(getClass().getSimpleName(), fps, screenshots.size(), toHexString(hashCode()));
  }
}
