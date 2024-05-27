package com.selenide.videorecorder;

import com.codeborne.selenide.WebDriverRunner;
import org.jobrunr.configuration.JobRunr;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.cron.Cron;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Timer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.webdriver;
import static org.jobrunr.scheduling.BackgroundJob.scheduleRecurrently;

/**
 * Created by Serhii Bryt
 * 07.05.2024 11:57
 **/
public class BrowserRecorder implements ITestListener, BeforeEachCallback, AfterEachCallback {
  private VideoRecorderScreenShot videoRecorder = null;
  private ScheduledThreadPoolExecutor timer;

  @Override
  public void onTestStart(ITestResult result) {
    if (!result.getMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(DisableVideoRecording.class)) {
      initRecorder();
    }
  }

  private void initRecorder() {
    if (!WebDriverRunner.hasWebDriverStarted()) {
      open();
    }
    videoRecorder = new VideoRecorderScreenShot(webdriver().object());
    timer = new ScheduledThreadPoolExecutor(1);
    timer.scheduleAtFixedRate(videoRecorder, 0, 1, TimeUnit.SECONDS);
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

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    if (videoRecorder != null) {
      if (!context.getTestMethod().get().isAnnotationPresent(DisableVideoRecording.class)) {
        stop();
      }
    }
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    if (!context.getTestMethod().get().isAnnotationPresent(DisableVideoRecording.class)) {
      initRecorder();
    }
  }
}
