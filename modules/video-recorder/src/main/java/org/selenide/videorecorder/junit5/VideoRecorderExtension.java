package org.selenide.videorecorder.junit5;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.selenide.videorecorder.core.NoVideo;
import org.selenide.videorecorder.core.RecordedVideos;
import org.selenide.videorecorder.core.VideoRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;

import static org.selenide.videorecorder.core.RecorderFileUtils.generateVideoFileName;

/**
 * Created by Serhii Bryt
 * 07.05.2024 11:57
 **/
public class VideoRecorderExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
  private static final Logger log = LoggerFactory.getLogger(VideoRecorderExtension.class);

  private static final RecordedVideos recordedVideos = new RecordedVideos();

  // TODO store in context
  @Nullable
  private VideoRecorder videoRecorder;

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    if (shouldRecordVideo(context)) {
      String testClass = context.getTestClass().map(tc -> tc.getSimpleName()).orElse(null);
      String testMethod = context.getTestMethod().map(tm -> tm.getName()).orElse(null);
      videoRecorder = new VideoRecorder(generateVideoFileName(testClass, testMethod));
      videoRecorder.start();
    }
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    if (videoRecorder != null) {
      videoRecorder.stop();
      recordedVideos.add(videoRecorder.videoFile());
      log.info("Video recorded: {}", videoRecorder.videoUrl().orElseThrow());
      videoRecorder = null;
    }
  }

  public static Optional<Path> getRecordedVideo() {
    return recordedVideos.getRecordedVideo();
  }

  private boolean shouldRecordVideo(ExtensionContext context) {
    return !context.getTestMethod()
      .map(m -> m.isAnnotationPresent(NoVideo.class))
      .orElse(false);
  }
}
