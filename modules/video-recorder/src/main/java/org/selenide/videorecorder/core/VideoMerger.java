package org.selenide.videorecorder.core;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.System.nanoTime;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

class VideoMerger {
  private static final Logger log = LoggerFactory.getLogger(VideoMerger.class);
  private final long threadId;
  private final VideoConfiguration config;
  private final Queue<Screenshot> screenshots;
  private final AtomicBoolean cancelled = new AtomicBoolean(false);
  private final Path videoFile;
  private final File screenshotsFolder;

  VideoMerger(long threadId, String videoId, VideoConfiguration config, File screenshotsFolder, Queue<Screenshot> screenshots) {
    this.threadId = threadId;
    this.config = config;
    this.screenshots = screenshots;
    this.screenshotsFolder = screenshotsFolder;
    videoFile = Path.of(config.videoFolder(), generateVideoFileName(videoId)).toAbsolutePath();
    RecordedVideos.add(threadId, videoFile);
  }

  private static String generateVideoFileName(String videoId) {
    return "video.%s.mp4".formatted(videoId);
  }

  public Path videoFile() {
    return videoFile;
  }

  public String videoUrl() {
    return videoFile().toUri().toString();
  }

  private void generateVideo() throws IOException {
    FFmpegBuilder builder = new FFmpegBuilder()
      .addInput(screenshotsFolder.getAbsolutePath() + "/screenshot.%d.png")
      .setVideoFilter("pad=iw+mod(iw\\,2):ih+mod(ih\\,2)")
      .addOutput(videoFile.toAbsolutePath().toString())
      .setVideoFrameRate(config.fps(), 1)
      .setVideoCodec("h264")
      .setVideoPixelFormat("yuv420p")
      .disableAudio()
      .setConstantRateFactor(config.crf())
      .done();

    FFmpeg ffmpeg = new FFmpeg();
    log.debug("Using {} of version {}", ffmpeg.getPath(), ffmpeg.version());
    FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
    executor.createJob(builder).run();
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

  /**
   * Complete video processing and save the video file
   */
  void finish() {
    if (cancelled.get()) {
      log.trace("Skip generating video because task has been cancelled");
      RecordedVideos.remove(threadId);
      return;
    }
    if (screenshots.isEmpty()) {
      log.trace("Skip generating video because no screenshots have been taken");
      RecordedVideos.remove(threadId);
      return;
    }
    else {
      log.debug("Generating video from {} screenshots in {} to file {}...", screenshots.size(), screenshotsFolder, videoFile);
      Path videoFolder = prepareVideoFolder(videoFile);
      log.debug("Created folder for video: {}", videoFolder.toAbsolutePath());
      try {
        long start = nanoTime();
        generateVideo();
        long durationMs = NANOSECONDS.toMillis(nanoTime() - start);
        log.debug("Generated video from {} screenshots in {} to file {} in {} ms.",
          screenshots.size(), screenshotsFolder, videoFile, durationMs);
      }
      catch (IOException e) {
        log.error("Failed to generate video {} from {} screenshots in {}", videoFile, screenshots.size(), screenshotsFolder, e);
        RecordedVideos.remove(threadId);
        throw new RuntimeException("Failed to generate video %s from %s screenshots in %s".formatted(
          videoFile, screenshots.size(), screenshotsFolder), e);
      }
      catch (Error | RuntimeException e) {
        log.error("Failed to generate video {} from {} screenshots in {}", videoFile, screenshots.size(), screenshotsFolder, e);
        RecordedVideos.remove(threadId);
        throw e;
      }
    }
    log.debug("Stopped video merger.");
  }

  public void cancel() {
    cancelled.set(true);
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
