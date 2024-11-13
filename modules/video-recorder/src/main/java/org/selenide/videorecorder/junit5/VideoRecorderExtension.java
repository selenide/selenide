package org.selenide.videorecorder.junit5;

import com.codeborne.selenide.WebDriverRunner;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.selenide.videorecorder.core.NoVideo;
import org.selenide.videorecorder.core.VideoRecorder;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.webdriver;
import static org.selenide.videorecorder.core.RecorderFileUtils.generateVideoFileName;

/**
 * Created by Serhii Bryt
 * 07.05.2024 11:57
 **/
public class VideoRecorderExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
  private VideoRecorder videoRecorder;
  private ScheduledThreadPoolExecutor timer;

  /**
   * Init video recorder and if there is no webdriver is running
   * call Selnide.open() method.
   */
  private void initRecorder(@Nullable String className, @Nullable String testName) {
    if (!WebDriverRunner.hasWebDriverStarted()) {
      open();
    }
    videoRecorder = new VideoRecorder(webdriver().object(), generateVideoFileName(className, testName));
    timer = new ScheduledThreadPoolExecutor(1);
    timer.scheduleAtFixedRate(videoRecorder, 0, 1, TimeUnit.SECONDS);
  }

  private void stop() {
    videoRecorder.stopRecording();
    timer.shutdown();
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    if (shouldRecordVideo(context)) {
      stop();
    }
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    if (shouldRecordVideo(context)) {
      String testClass = context.getTestClass().map(tc -> tc.getSimpleName()).orElse(null);
      String testMethod = context.getTestMethod().map(tm -> tm.getName()).orElse(null);
      initRecorder(testClass, testMethod);
    }
  }

  private boolean shouldRecordVideo(ExtensionContext context) {
    return !context.getTestMethod()
      .map(m -> m.isAnnotationPresent(NoVideo.class))
      .orElse(false);
  }
}
