package org.selenide.videorecorder.testng;

import org.selenide.videorecorder.core.NoVideo;
import org.selenide.videorecorder.core.RecordedVideos;
import org.selenide.videorecorder.core.VideoRecorder;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Path;
import java.util.Optional;

import static java.lang.Thread.currentThread;

/**
 * Created by Serhii Bryt
 * 07.05.2024 11:57
 */
public class VideoRecorderListener implements ITestListener {
  private static final String NAME = "VIDEO_RECORDER";

  @Override
  public void onTestStart(ITestResult result) {
    result.removeAttribute(NAME);
    if (shouldRecordVideo(result)) {
      VideoRecorder videoRecorder = new VideoRecorder();
      result.setAttribute(NAME, videoRecorder);
      videoRecorder.start();
    }
  }

  @Override
  public void onTestFailure(ITestResult result) {
    finish(result);
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    finish(result);
  }

  private void finish(ITestResult result) {
    VideoRecorder videoRecorder = (VideoRecorder) result.getAttribute(NAME);
    if (videoRecorder != null) {
      videoRecorder.stop();
      result.removeAttribute(NAME);
    }
  }

  private static boolean shouldRecordVideo(ITestResult result) {
    return !result.getMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(NoVideo.class);
  }

  public static Optional<Path> getRecordedVideo() {
    return RecordedVideos.getRecordedVideo(currentThread().getId());
  }
}
