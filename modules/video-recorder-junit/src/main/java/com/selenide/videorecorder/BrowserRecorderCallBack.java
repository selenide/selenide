package com.selenide.videorecorder;

import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.Selenide.webdriver;

/**
 * Created by Serhii Bryt
 * 07.05.2024 11:57
 **/
public class BrowserRecorderCallBack implements  BeforeTestExecutionCallback, AfterTestExecutionCallback {
  private VideoRecorderScreenShot videoRecorder = null;
  private ScheduledThreadPoolExecutor timer;

  /**
   * Init video recorder and if there is no webdriver is running
   * call Selnide.open() method.
   */
  private void initRecorder(String className, String testName) {
    if (!WebDriverRunner.hasWebDriverStarted()) {
      open();
    }
    sleep(1000);
    videoRecorder = new VideoRecorderScreenShot(webdriver().object(), className, testName);
    timer = new ScheduledThreadPoolExecutor(1);
    timer.scheduleAtFixedRate(videoRecorder, 0, 1, TimeUnit.SECONDS);
  }

  private void stop() {
    videoRecorder.stopRecording();
    videoRecorder.cancel();
    timer.shutdownNow();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    if (videoRecorder != null) {
      if (!context.getTestMethod().get().isAnnotationPresent(DisableVideoRecording.class)) {
        stop();
      }
    }
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) throws Exception {
    if (!context.getTestMethod().get().isAnnotationPresent(DisableVideoRecording.class)) {
      initRecorder(context.getTestClass().get().getSimpleName(), context.getTestMethod().get().getName());
    }
  }
}
