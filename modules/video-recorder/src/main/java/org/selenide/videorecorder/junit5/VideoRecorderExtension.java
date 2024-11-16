package org.selenide.videorecorder.junit5;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.selenide.videorecorder.core.NoVideo;
import org.selenide.videorecorder.core.RecordedVideos;
import org.selenide.videorecorder.core.VideoRecorder;

import java.nio.file.Path;
import java.util.Optional;

import static java.lang.Thread.currentThread;

/**
 * Created by Serhii Bryt
 * 07.05.2024 11:57
 **/
public class VideoRecorderExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
  // TODO store in context
  @Nullable
  private VideoRecorder videoRecorder;

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    if (shouldRecordVideo(context)) {
      videoRecorder = new VideoRecorder();
      videoRecorder.start();
    }
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    if (videoRecorder != null) {
      videoRecorder.stop();
      videoRecorder = null;
    }
  }

  public static Optional<Path> getRecordedVideo() {
    return RecordedVideos.getRecordedVideo(currentThread().getId());
  }

  private boolean shouldRecordVideo(ExtensionContext context) {
    return !context.getTestMethod()
      .map(m -> m.isAnnotationPresent(NoVideo.class))
      .orElse(false);
  }
}
