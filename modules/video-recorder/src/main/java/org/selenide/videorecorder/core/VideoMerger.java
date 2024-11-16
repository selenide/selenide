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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.requireNonNull;
import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P;

class VideoMerger extends TimerTask {
  private static final Logger log = LoggerFactory.getLogger(VideoMerger.class);
  private static final AtomicLong videoFileCounter = new AtomicLong(0);
  private final long threadId;
  private final int fps;
  private final int crf;
  private final Queue<Screenshot> screenshots;

  @Nullable
  private FFmpegFrameRecorder recorder;

  @Nullable
  private Path videoFile;

  VideoMerger(long threadId, int fps, int crf, Queue<Screenshot> screenshots) {
    this.threadId = threadId;
    this.fps = fps;
    this.crf = crf;
    this.screenshots = screenshots;
  }

  public Optional<Path> videoFile() {
    return Optional.ofNullable(videoFile);
  }

  public Optional<String> videoUrl() {
    return videoFile().map(f -> f.toUri().toString());
  }

  @Override
  public void run() {
    if (screenshots.size() < 2) return;

    try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
      while (true) {
        if (screenshots.size() < 2) break;
        Screenshot current = screenshots.poll();
        Screenshot next = screenshots.element();

        log.debug("Processing {} (queue size: {})", current, screenshots.size());

        try {
          long start = currentTimeMillis();
          FFmpegFrameRecorder videoRecorder = getVideoRecorder(current);
          int framesCount = framesCount(fps, current.timestamp, next.timestamp);
          validateFramesCount(framesCount, current, next);

          try (Frame frame = screenshotToFrame(converter, current.screenshot)) {
            log.debug("Adding {} to video x {} times (queue size: {}) ...", current, framesCount, screenshots.size());
            for (int i = 0; i < framesCount; i++) {
              videoRecorder.record(frame);
            }
          }

          long end = currentTimeMillis();
          log.debug("Added {} x {} times in {} ms. (queue size: {})", current, framesCount, end - start, screenshots.size());

          if (current.isEnd()) {
            log.debug("Detected end of screenshots queue: {}", current);
            finish();
            cancel();
            break;
          }
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

  private void validateFramesCount(int framesCount, Screenshot current, Screenshot next) {
    if (framesCount < 1 || framesCount > fps) {
      log.warn("Strange: framesCount={}: fps={}, screenshot.ts={}, next.ts={}",
        framesCount, fps, current.timestamp, next.timestamp);
    }
  }

  static int framesCount(int fps, long startMoment, long endMoment) {
    return (int) ((endMoment - startMoment) * fps / 1000);
  }

  private FFmpegFrameRecorder getVideoRecorder(Screenshot screenshot) {
    if (recorder == null) {
      recorder = initVideoRecording(screenshot);
    }
    return requireNonNull(recorder);
  }

  private FFmpegFrameRecorder initVideoRecording(Screenshot screenshot) {
    String fileName = currentTimeMillis() + "." + videoFileCounter.getAndIncrement() + ".webm";
    videoFile = Path.of(screenshot.config.reportsFolder(), fileName).toAbsolutePath();

    Dimension window = screenshot.window;
    log.debug("Start FFMpeg video recorder of size {}x{} in file {}", window.width, window.height, videoFile.toAbsolutePath());

    Path videoFolder = prepareVideoFolder(videoFile);
    log.debug("Created folder for video: {}", videoFolder.toAbsolutePath());

    RecordedVideos.add(threadId, videoFile);

    FFmpegFrameRecorder rec = new FFmpegFrameRecorder(videoFile.toFile(), window.width, window.height);
    rec.setFormat("webm");
    rec.setVideoCodecName("libx264");
    rec.setVideoOption("crf", String.valueOf(crf));
    rec.setPixelFormat(AV_PIX_FMT_YUV420P);
    rec.setFrameRate(fps);
    try {
      rec.start();
      log.debug("Started FFMpeg video recorder of size {}x{} in file {}", window.width, window.height, videoFile.toAbsolutePath());
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
    try (InputStream in = new ByteArrayInputStream(screenshot)) {
      return converter.getFrame(ImageIO.read(in), 1.0, true);
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
        log.debug("Stopping frame recorder ...");
        recorder.stop();
        recorder = null;
        log.debug("Stopped frame recorder.");
      }
      catch (FFmpegFrameRecorder.Exception e) {
        log.error("Failed to stop video recorder", e);
        throw new RuntimeException(e);
      }
    }
  }
}
