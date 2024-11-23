package org.selenide.videorecorder.junit5;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.selenide.videorecorder.core.NoVideo;
import org.selenide.videorecorder.core.RecordedVideos;
import org.selenide.videorecorder.core.Video;
import org.selenide.videorecorder.core.VideoConfiguration;
import org.selenide.videorecorder.core.VideoRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.Optional;

import static java.lang.Thread.currentThread;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;
import static org.selenide.videorecorder.core.VideoSaveMode.ALL;

/**
 * Created by Serhii Bryt
 * 07.05.2024 11:57
 **/
public class VideoRecorderExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
  private static final Logger log = LoggerFactory.getLogger(VideoRecorderExtension.class);
  private static final VideoConfiguration config = new VideoConfiguration();
  private static final ExtensionContext.Namespace namespace = create(VideoRecorderExtension.class);
  private static final String NAME = "VIDEO_RECORDER";

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    RecordedVideos.remove(currentThread().getId());

    if (shouldRecordVideo(context)) {
      VideoRecorder videoRecorder = new VideoRecorder();
      videoRecorder.start();
      context.getStore(namespace).put(NAME, videoRecorder);
    }
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    boolean testFailed = context.getExecutionException().isPresent();
    afterTestExecution(context, testFailed);
  }

  protected void afterTestExecution(ExtensionContext context, boolean testFailed) {
    VideoRecorder videoRecorder = context.getStore(namespace).remove(NAME, VideoRecorder.class);
    //noinspection ConstantValue
    if (videoRecorder != null) {
      if (config.saveMode() == ALL || testFailed) {
        videoRecorder.finish();
      }
      else {
        videoRecorder.cancel();
      }
    }
  }

  public static Optional<Path> getRecordedVideo() {
    return RecordedVideos.getRecordedVideo(currentThread().getId());
  }

  private boolean shouldRecordVideo(ExtensionContext context) {
    if (!config.videoEnabled()) {
      log.debug("Video recorder disabled");
      return false;
    }
    return switch (config.mode()) {
      case ALL -> !isAnnotated(context, NoVideo.class);
      case ANNOTATED -> isAnnotated(context, Video.class);
    };
  }

  private Boolean isAnnotated(ExtensionContext context, Class<? extends Annotation> annotation) {
    return context.getTestMethod()
      .map(m -> m.isAnnotationPresent(annotation))
      .orElse(false);
  }
}
