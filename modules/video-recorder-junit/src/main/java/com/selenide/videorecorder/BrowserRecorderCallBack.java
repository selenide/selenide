package com.selenide.videorecorder;

import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.webdriver;

/**
 * Created by Serhii Bryt
 * 07.05.2024 11:57
 **/
public class BrowserRecorderCallBack implements BeforeEachCallback, AfterEachCallback {
  private VideoRecorderScreenShot videoRecorder = null;
  private ScheduledThreadPoolExecutor timer;


  private void initRecorder() {
    if (!WebDriverRunner.hasWebDriverStarted()) {
      open();
    }
    videoRecorder = new VideoRecorderScreenShot(webdriver().object());
    timer = new ScheduledThreadPoolExecutor(1);
    timer.scheduleAtFixedRate(videoRecorder, 0, 1, TimeUnit.SECONDS);
  }

  private void stop() {
    videoRecorder.stopRecording();
    videoRecorder.cancel();
    timer.shutdownNow();
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
