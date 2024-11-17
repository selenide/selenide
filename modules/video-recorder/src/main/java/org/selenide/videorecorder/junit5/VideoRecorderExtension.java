package org.selenide.videorecorder.junit5;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.selenide.videorecorder.core.NoVideo;
import org.selenide.videorecorder.core.RecordedVideos;
import org.selenide.videorecorder.core.VideoRecorder;

import java.nio.file.Path;
import java.util.Optional;

import static java.lang.Thread.currentThread;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

/**
 * Created by Serhii Bryt
 * 07.05.2024 11:57
 **/
public class VideoRecorderExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
  private static final ExtensionContext.Namespace namespace = create(VideoRecorderExtension.class);
  private static final String NAME = "VIDEO_RECORDER";

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    if (shouldRecordVideo(context)) {
      VideoRecorder videoRecorder = new VideoRecorder();
      videoRecorder.start();
      context.getStore(namespace).put(NAME, videoRecorder);
    }
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    VideoRecorder videoRecorder = context.getStore(namespace).remove(NAME, VideoRecorder.class);
    //noinspection ConstantValue
    if (videoRecorder != null) {
      videoRecorder.stop();
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
