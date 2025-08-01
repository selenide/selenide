package org.selenide.videorecorder.core;

import com.codeborne.selenide.impl.AttachmentHandler;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

import static com.codeborne.selenide.impl.FileHelper.deleteFolder;
import static com.codeborne.selenide.impl.FileHelper.ensureFolderExists;
import static com.codeborne.selenide.impl.Plugins.inject;
import static com.codeborne.selenide.impl.ThreadNamer.named;
import static java.lang.Integer.toHexString;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.nanoTime;
import static java.lang.Thread.currentThread;
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

  private final long threadId = currentThread().getId();
  public final String videoId = "%s.%s.%s".formatted(currentTimeMillis(), videoCounter.getAndIncrement(), threadId);
  private final ScheduledThreadPoolExecutor screenshooter =
    new ThreadPool(1, named("%s:screen-shooter:%s:".formatted(currentThread().getName(), videoId)));
  private final int fps;
  private final Queue<Screenshot> screenshots = new ConcurrentLinkedQueue<>();
  private final File screenshotsFolder;
  private final ScreenShooter screenShooterTask;
  @Nullable
  private ScheduledFuture<?> future;
  private final VideoMerger videoMerger;

  public VideoRecorder() {
    fps = config.fps();
    screenshotsFolder = createScreenshotsFolder(videoId);
    screenShooterTask = new ScreenShooter(threadId, screenshotsFolder, screenshots);
    videoMerger = new VideoMerger(threadId, videoId, config, screenshotsFolder, screenshots);
    log.debug("Created video recorder for video {}, folder: {}", videoId, screenshotsFolder);
  }

  private static File createScreenshotsFolder(String videoId) {
    File screenshotsFolder = new File(config.videoFolder(), "video.%s.screenshots".formatted(videoId));
    ensureFolderExists(screenshotsFolder);
    if (!config.keepScreenshots()) {
      screenshotsFolder.deleteOnExit();
    }
    log.debug("Created screenshots folder {} for video {}", screenshotsFolder.getAbsolutePath(), videoId);
    return screenshotsFolder;
  }

  public String videoUrl() {
    return videoMerger.videoUrl();
  }

  public void start() {
    log.info("Starting screenshooter {} every {} nanoseconds to achieve fps {}", videoId, delayBetweenFramesNanos(), fps);
    future = screenshooter.scheduleAtFixedRate(screenShooterTask, 0, delayBetweenFramesNanos(), NANOSECONDS);
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
      log.debug("  Cancel screen shooter task");
      boolean cancelled = screenShooterTask.cancel();
      log.debug("  Cancel screen shooter future");
      future.cancel(false);
      log.debug("  Shutdown screen shooter (cancelled: {})", cancelled);
      screenshooter.shutdown();
      stop("Screenshooter", screenshooter, 2000);
      log.debug("  Shutdown screen shooter now");
      screenshooter.shutdownNow();

      log.debug("  Finish video merger");
      videoMerger.finish();

      log.info("Video recorded: {}", videoUrl());
      attachmentHandler.attach(videoMerger.videoFile().toFile());

      if (!config.keepScreenshots()) {
        deleteFolder(screenshotsFolder);
      }
    }
    catch (InterruptedException e) {
      currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  /**
   * Stop video processing and delete the video file
   */
  public void cancel() {
    log.debug("Cancel video recorder task");
    screenShooterTask.cancel();
    log.debug("Cancel video recorder future");
    future.cancel(false);
    log.debug("shutdown now video recorder thread pool");
    screenshooter.shutdownNow();
    videoMerger.rollback();
    videoMerger.cancel();
  }

  private void stop(String name, ScheduledThreadPoolExecutor threadPool, long timeoutMs) throws InterruptedException {
    long start = nanoTime();
    if (!threadPool.awaitTermination(timeoutMs, MILLISECONDS)) {
      log.warn("{} thread hasn't completed in {} ms. (pool size: {}, queue size: {}, " +
               "active count: {}, max pool size: {}, task count: {}, terminating: {}, terminated: {} " +
               "getContinueExistingPeriodicTasksAfterShutdownPolicy: {}, " +
               "getExecuteExistingDelayedTasksAfterShutdownPolicy: {})",
        name, timeoutMs,
        threadPool.getPoolSize(), threadPool.getQueue().size(), threadPool.getActiveCount(),
        threadPool.getMaximumPoolSize(),
        threadPool.getTaskCount(),
        threadPool.isTerminating(),
        threadPool.isTerminated(),
        threadPool.getContinueExistingPeriodicTasksAfterShutdownPolicy(),
        threadPool.getExecuteExistingDelayedTasksAfterShutdownPolicy()
      );
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
