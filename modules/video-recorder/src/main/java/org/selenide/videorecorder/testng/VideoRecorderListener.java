package org.selenide.videorecorder.testng;

import org.jspecify.annotations.Nullable;
import org.selenide.videorecorder.core.NoVideo;
import org.selenide.videorecorder.core.RecordedVideos;
import org.selenide.videorecorder.core.VideoRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  private static final Logger log = LoggerFactory.getLogger(VideoRecorderListener.class);

  @Nullable
  private VideoRecorder videoRecorder;

  @Override
  public void onTestStart(ITestResult result) {
    if (shouldRecordVideo(result)) {
      videoRecorder = new VideoRecorder();
      videoRecorder.start();
    }
  }

  @Override
  public void onTestFailure(ITestResult result) {
    finish();
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    finish();
  }

  private void finish() {
    if (videoRecorder != null) {
      videoRecorder.stop();
      videoRecorder = null;
    }
  }

  private static boolean shouldRecordVideo(ITestResult result) {
    return !result.getMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(NoVideo.class);
  }

  public static Optional<Path> getRecordedVideo() {
    return RecordedVideos.getRecordedVideo(currentThread().getId());
  }
}
