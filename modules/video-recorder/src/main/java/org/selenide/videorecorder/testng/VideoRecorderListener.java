package org.selenide.videorecorder.testng;

import com.codeborne.selenide.WebDriverRunner;
import org.selenide.videorecorder.core.NoVideo;
import org.selenide.videorecorder.core.RecorderFileUtils;
import org.selenide.videorecorder.core.VideoRecorder;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.webdriver;

/**
 * Created by Serhii Bryt
 * 07.05.2024 11:57
 */
public class VideoRecorderListener implements ITestListener {
  private VideoRecorder videoRecorder;
  private ScheduledThreadPoolExecutor timer;

  @Override
  public void onTestStart(ITestResult result) {
    if (shouldRecordVideo(result)) {
      initRecorder(result.getTestClass().getRealClass().getSimpleName(), result.getMethod().getMethodName());
    }
  }

  private void initRecorder(String testClassName, String testName) {
    if (!WebDriverRunner.hasWebDriverStarted()) {
      open();
    }
    videoRecorder = new VideoRecorder(webdriver().object(),
      RecorderFileUtils.generateVideoFileName(testClassName, testName));
    timer = new ScheduledThreadPoolExecutor(1);
    timer.scheduleAtFixedRate(videoRecorder, 0, 1000, TimeUnit.MILLISECONDS);
  }

  @Override
  public void onTestFailure(ITestResult result) {
    if (shouldRecordVideo(result)) {
      stop();
    }
  }

  private void stop() {
    videoRecorder.stopRecording();
    timer.shutdownNow();
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    if (shouldRecordVideo(result)) {
      stop();
    }
  }

  private static boolean shouldRecordVideo(ITestResult result) {
    return !result.getMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(NoVideo.class);
  }
}
