package org.selenide.videorecorder.testng;

import org.selenide.videorecorder.core.NoVideo;
import org.selenide.videorecorder.core.RecordedVideos;
import org.selenide.videorecorder.core.Video;
import org.selenide.videorecorder.core.VideoConfiguration;
import org.selenide.videorecorder.core.VideoRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.Optional;

import static java.lang.Thread.currentThread;
import static org.selenide.videorecorder.core.VideoSaveMode.ALL;

/**
 * Created by Serhii Bryt
 * 07.05.2024 11:57
 */
public class VideoRecorderListener implements ITestListener {
  private static final Logger log = LoggerFactory.getLogger(VideoRecorderListener.class);
  private static final VideoConfiguration config = new VideoConfiguration();
  private static final String NAME = "VIDEO_RECORDER";

  @Override
  public void onTestStart(ITestResult result) {
    result.removeAttribute(NAME);
    RecordedVideos.remove(currentThread().getId());

    if (shouldRecordVideo(result)) {
      VideoRecorder videoRecorder = new VideoRecorder();
      result.setAttribute(NAME, videoRecorder);
      log.info("Starting video recorder {} for test {} in thread {}", videoRecorder, result.getName(), currentThread().getName());
      videoRecorder.start();
    } else {
      log.info("Not starting video recorder for test {} in thread {}", result.getName(), currentThread().getName());
    }
  }

  @Override
  public void onTestFailure(ITestResult result) {
    log.info("onTestFailure {}", result);
    finish(result, true);
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    log.info("onTestSuccess {}", result);
    finish(result, false);
  }

  protected void finish(ITestResult result, boolean testFailed) {
    VideoRecorder recorder = (VideoRecorder) result.removeAttribute(NAME);
    if (recorder != null) {
      long threadId = currentThread().getId();
      if (config.saveMode() == ALL || testFailed) {
        log.info("Stopping recorder {} for test {} in thread {}: saving video", recorder, result.getName(), threadId);
        recorder.finish();
      }
      else {
        log.info("Stopping recorder {} for test {} in thread {}: not saving video", recorder, result.getName(), threadId);
        recorder.cancel();
      }
    } else {
      log.info("Not found video recorder for test {} in thread {}", result.getName(), currentThread().getName());
    }
  }

  private static boolean shouldRecordVideo(ITestResult context) {
    if (!config.videoEnabled()) {
      log.debug("Video recorder disabled");
      return false;
    }

    return switch (config.mode()) {
      case ALL -> !isAnnotated(context, NoVideo.class);
      case ANNOTATED -> isAnnotated(context, Video.class);
    };
  }

  private static boolean isAnnotated(ITestResult result, Class<? extends Annotation> annotation) {
    return result.getMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(annotation);
  }

  public static Optional<Path> getRecordedVideo() {
    return RecordedVideos.getRecordedVideo(currentThread().getId());
  }
}
