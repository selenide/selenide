package com.selenide.videorecorder;

import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.webdriver;

/**
 * Created by Serhii Bryt
 * 07.05.2024 11:57
 **/
public class BrowserRecorderCallBack implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
  private VideoRecorderScreenShot videoRecorder = null;
  private ScheduledThreadPoolExecutor timer;
  private Boolean shouldRecordVideo = false;

  /**
   * Init video recorder and if there is no webdriver is running
   * call Selnide.open() method.
   */
  private void initRecorder(String className, String testName) {
    if (!WebDriverRunner.hasWebDriverStarted()) {
      open();
    }
    videoRecorder = new VideoRecorderScreenShot(webdriver().object(),
      RecorderFileUtils.generateVideoFileName(className, testName));
    timer = new ScheduledThreadPoolExecutor(1);
    timer.scheduleAtFixedRate(videoRecorder, 0, 1, TimeUnit.SECONDS);
  }

  private void stop() {
    videoRecorder.stopRecording();
    timer.shutdown();
  }


  @Override
  public void afterTestExecution(ExtensionContext context) {
    if (shouldRecordVideo) {
      stop();
    }
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    shouldRecordVideo = !context.getTestMethod().get().isAnnotationPresent(DisableVideoRecording.class);
    if (shouldRecordVideo) {
      initRecorder(context.getTestClass().get().getSimpleName(), context.getTestMethod().get().getName());
    }
  }
}
