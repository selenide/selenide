package org.selenide.videorecorder.core;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Dimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Queue;
import java.util.TimerTask;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.nanoTime;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P;

class VideoMerger extends TimerTask {
  private static final Logger log = LoggerFactory.getLogger(VideoMerger.class);
  private final int fps;
  private final int crf;
  private final Queue<Screenshot> screenshots;

  @Nullable
  private FFmpegFrameRecorder recorder;

  VideoMerger(int fps, int crf, Queue<Screenshot> screenshots) {
    this.fps = fps;
    this.crf = crf;
    this.screenshots = screenshots;
  }

  @Override
  public void run() {
    if (screenshots.size() < 2) return;

    try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
      while (true) {
        if (screenshots.size() < 2) break;
        Screenshot screenshot = screenshots.poll();
        Screenshot next = screenshots.element();

        log.info("Processing screenshot {} (end: {}), queue size: {}", screenshot.timestamp, screenshot.isEnd(), screenshots.size());

        if (screenshot.isEnd()) {
          log.info("Detected end of screenshots queue: {}", screenshot);
          finish();
          cancel();
          break;
        }

        try {
          long start = currentTimeMillis();
          FFmpegFrameRecorder videoRecorder = getVideoRecorder(screenshot, screenshot.videoFile);
          int framesCount = framesCount(fps, screenshot.timestamp, next.timestamp);
          if (framesCount < 1 || framesCount > fps) {
            log.warn("Strange: framesCount={}: fps={}, screenshot.ts={}, next.ts={}", framesCount, fps, screenshot.timestamp, next.timestamp);
          }

          try (Frame frame = screenshotToFrame(converter, screenshot.screenshot)) {
            log.debug("Adding screenshot {} to video x {} times at {} ms. (frames queue size: {}) ...",
              screenshot.timestamp, framesCount, start, screenshots.size());
            for (int i = 0; i < framesCount; i++) {
              videoRecorder.record(frame);
            }
          }

          long end = currentTimeMillis();
          log.debug("Added screenshot {} x {} times in {} ms. (frames queue size: {})",
            screenshot.timestamp, framesCount, end - start, screenshots.size());
        }
        catch (FFmpegFrameRecorder.Exception e) {
          log.error("Failed to add screenshot to video", e);
          throw new RuntimeException(e);
        }
        catch (Error | RuntimeException e) {
          log.error("Failed to add screenshot to video", e);
          throw e;
        }
      }
    }
  }

  static int framesCount(int fps, long startMoment, long endMoment) {
    return (int) ((endMoment - startMoment) * fps / 1000);
  }

  private FFmpegFrameRecorder getVideoRecorder(Screenshot screenshot, Path videoFile) {
    if (recorder == null) {
      recorder = initVideoRecording(screenshot, videoFile);
    }
    return requireNonNull(recorder);
  }

  private FFmpegFrameRecorder initVideoRecording(Screenshot screenshot, Path videoFile) {
    Dimension window = screenshot.window;
    log.info("Start FFMpeg video recorder of size {}x{} in file {}", window.width, window.height, videoFile.toAbsolutePath());

    Path videoFolder = prepareVideoFolder(requireNonNull(videoFile));
    log.info("Created folder for video: {}", videoFolder.toAbsolutePath());

    FFmpegFrameRecorder rec = new FFmpegFrameRecorder(videoFile.toFile(), window.width, window.height);
    rec.setFormat("webm");
    rec.setVideoCodecName("libx264");
    rec.setVideoOption("crf", String.valueOf(crf));
    rec.setPixelFormat(AV_PIX_FMT_YUV420P);
    rec.setFrameRate(fps);
    try {
      rec.start();
      log.info("Started FFMpeg video recorder of size {}x{} in file {}", window.width, window.height, videoFile.toAbsolutePath());
      return rec;
    }
    catch (FFmpegFrameRecorder.Exception e) {
      String message = "Failed to start video recording in file %s".formatted(videoFile.toAbsolutePath());
      log.error(message, e);
      throw new RuntimeException(message, e);
    }
    catch (Error | RuntimeException e) {
      String message = "Failed to start video recording in file %s".formatted(videoFile.toAbsolutePath());
      log.error(message, e);
      throw e;
    }
  }

  private static Path prepareVideoFolder(Path videoFile) {
    Path videoFolder = requireNonNull(videoFile.getParent());
    try {
      return Files.createDirectories(videoFolder);
    }
    catch (IOException e) {
      String message = "Failed to create video folder " + videoFolder;
      log.error(message, e);
      throw new RuntimeException(message, e);
    }
  }

  private static Frame screenshotToFrame(Java2DFrameConverter converter, byte[] screenshot) {
    long start = nanoTime();
    try {
      log.info("Converting screenshot to frame...");
      Frame result = converter.getFrame(ImageIO.read(new ByteArrayInputStream(screenshot)), 1.0, true);
      long end = nanoTime();
      log.info("Converted screenshot to frame of size {}x{} in {} ms.",
        result.imageWidth, result.imageHeight, NANOSECONDS.toMillis(end - start));
      return result;
    }
    catch (IOException e) {
      log.error("Failed to convert screenshot to frame", e);
      throw new RuntimeException("Failed to convert screenshot to frame", e);
    }
    catch (Error | RuntimeException e) {
      log.error("Failed to convert screenshot to frame", e);
      throw e;
    }
  }

  private void finish() {
    if (recorder != null) {
      try {
        log.error("Stopping video recorder ...");
        recorder.stop();
        recorder = null;
        log.error("Stopped video recorder.");
      }
      catch (FFmpegFrameRecorder.Exception e) {
        log.error("Failed to stop video recorder", e);
        throw new RuntimeException(e);
      }
    }
  }
}
