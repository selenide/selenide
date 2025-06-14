package org.selenide.videorecorder.core;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Dimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static java.awt.image.BufferedImage.TYPE_3BYTE_BGR;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.nanoTime;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P;

class VideoMerger extends TimerTask {
  private static final Logger log = LoggerFactory.getLogger(VideoMerger.class);
  private static final AtomicLong videoFileCounter = new AtomicLong(0);
  private final long threadId;
  private final int fps;
  private final int crf;
  private final Queue<Screenshot> screenshots;
  private final AtomicBoolean cancelled = new AtomicBoolean(false);

  @Nullable
  private FFmpegFrameRecorder recorder;

  private final Path videoFile;

  VideoMerger(long threadId, String folder, int fps, int crf, Queue<Screenshot> screenshots) {
    this.threadId = threadId;
    this.fps = fps;
    this.crf = crf;
    this.screenshots = screenshots;
    videoFile = Path.of(folder, generateVideoFileName()).toAbsolutePath();
    RecordedVideos.add(threadId, videoFile);
  }

  private String generateVideoFileName() {
    return currentTimeMillis() + "." + videoFileCounter.getAndIncrement() + ".webm";
  }

  public Path videoFile() {
    return videoFile;
  }

  public String videoUrl() {
    return videoFile().toUri().toString();
  }

  @Override
  public void run() {
    if (screenshots.size() < 2) {
      log.trace("Skip processing because of empty queue (queue size: {})", screenshots.size());
      return;
    }

    Screenshot current = screenshots.poll();
    Screenshot next = screenshots.element();

    log.debug("Processing {} (queue size: {})", current, screenshots.size());

    try {
      long start = nanoTime();
      FFmpegFrameRecorder videoRecorder = getVideoRecorder(current);
      int framesCount = Math.max(1, framesCount(fps, current.timestamp, next.timestamp));

      try (Java2DFrameConverter converter = new Java2DFrameConverter();
           Frame frame = screenshotToFrame(converter, current.screenshot)) {
        log.trace("Adding {} to video x {} times (queue size: {}) ...", current, framesCount, screenshots.size());
        for (int i = 0; !cancelled.get() && i < framesCount; i++) {
          videoRecorder.record(frame);
        }
      }

      long durationMs = NANOSECONDS.toMillis(nanoTime() - start);
      log.debug("Added {} to video x {} times in {} ms. (queue size: {})", current, framesCount, durationMs, screenshots.size());

      current.screenshot.dispose();

      if (current.isEnd()) {
        log.debug("Detected end of screenshots queue: {}", current);
        cancel();
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

  static int framesCount(int fps, long startMomentNanos, long endMomentNanos) {
    return (int) ((endMomentNanos - startMomentNanos) * fps / SECONDS.toNanos(1));
  }

  private FFmpegFrameRecorder getVideoRecorder(Screenshot screenshot) {
    if (recorder == null) {
      recorder = initVideoRecording(screenshot);
    }
    return requireNonNull(recorder);
  }

  private FFmpegFrameRecorder initVideoRecording(Screenshot screenshot) {
    Dimension window = screenshot.window;
    log.debug("Start FFMpeg video recorder of size {}x{} in file {}", window.width, window.height, videoFile.toAbsolutePath());

    Path videoFolder = prepareVideoFolder(videoFile);
    log.debug("Created folder for video: {}", videoFolder.toAbsolutePath());

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

  private static Frame screenshotToFrame(Java2DFrameConverter converter, ImageSource screenshot) {
    try {
      BufferedImage rgbImage = removeAlpha(screenshot.getImage());
      return converter.getFrame(rgbImage, 1.0, false);
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

  private static BufferedImage removeAlpha(BufferedImage image) {
    if (!image.getColorModel().hasAlpha()) {
      return image;
    }

    BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), TYPE_3BYTE_BGR);
    copy(image, rgbImage);
    return rgbImage;
  }

  private static void copy(BufferedImage source, BufferedImage target) {
    Graphics2D g = target.createGraphics();
    try {
      g.drawImage(source, 0, 0, null);
    }
    finally {
      g.dispose();
    }
  }

  /**
   * Complete video processing and save the video file
   */
  void finish() {
    if (recorder != null) {
      while (!cancelled.get() && screenshots.size() > 1) {
        run();
      }
      try {
        log.debug("Stopping video merger ...");
        recorder.stop();
        recorder = null;
        log.debug("Stopped video merger.");
      }
      catch (FFmpegFrameRecorder.Exception e) {
        log.error("Failed to stop video recorder", e);
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  @CanIgnoreReturnValue
  public boolean cancel() {
    cancelled.set(true);
    return super.cancel();
  }

  /**
   * Stop video processing and delete the video file
   */
  public void rollback() {
    log.debug("Cancelling video recorder ...");
    cancel();
    finish();
    deleteVideoFile();
    RecordedVideos.remove(threadId);
  }

  private void deleteVideoFile() {
    try {
      Files.deleteIfExists(videoFile);
    }
    catch (IOException e) {
      log.error("Failed to delete video file {}", videoFile, e);
    }
  }
}
