package com.selenide.videorecorder;

import com.codeborne.selenide.WebDriverRunner;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.webdriver;

/**
 * Created by Serhii Bryt
 * 07.05.2024 11:57
 **/
public class BrowserRecorderListener implements ITestListener {
  private VideoRecorderScreenShot videoRecorder = null;
  private ScheduledThreadPoolExecutor timer;

  @Override
  public void onTestStart(ITestResult result) {
    if (!result.getMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(DisableVideoRecording.class)) {
      initRecorder(result.getTestClass().getRealClass().getSimpleName(), result.getMethod().getMethodName());
    }
  }

  private void initRecorder(String testClassName, String testName) {
    if (!WebDriverRunner.hasWebDriverStarted()) {
      open();
    }
    videoRecorder = new VideoRecorderScreenShot(webdriver().object(), testClassName, testName);
    timer = new ScheduledThreadPoolExecutor(1);
    timer.scheduleAtFixedRate(videoRecorder, 0, 1000, TimeUnit.MILLISECONDS);
  }

  @Override
  public void onTestFailure(ITestResult result) {
    if (videoRecorder != null) {
      stop();
    }
  }

  private void stop() {
    videoRecorder.stopRecording();
    videoRecorder.cancel();
    timer.shutdownNow();
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    if (videoRecorder != null) {
      if (!result.getMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(DisableVideoRecording.class)) {
        stop();
      }
    }
  }
}
