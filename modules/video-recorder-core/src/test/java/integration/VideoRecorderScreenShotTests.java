package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.DragAndDropOptions;
import com.selenide.videorecorder.VideoRecorderScreenShot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.MutableCapabilities;

import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class VideoRecorderScreenShotTests {
  VideoRecorderScreenShot videoRecorder;
  ScheduledThreadPoolExecutor executor;


  @BeforeAll
  public static void beforeAll() {
    MutableCapabilities mutableCapabilities = new MutableCapabilities();
    mutableCapabilities.setCapability("webSocketUrl", true);
    Configuration.browserCapabilities = mutableCapabilities;
    open();
  }

  @AfterEach
  public void afterEach() {
    stopRecordingAndCloseDriver();
    checkVideoLengthInTime();
  }

  @Test
  public void videoFileShouldExistsAndNotEmptyTestCore(TestInfo testInfo) {
    initRecorder(testInfo);
    open("file://" + this.getClass().getClassLoader().getResource("draggable.html").getPath());
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div2"));
    sleep(3000);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div1"));
    sleep(3000);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div2"));
    sleep(3000);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div1"));
    sleep(3000);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div2"));
    sleep(3000);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div1"));
    sleep(3000);
  }

  private void initRecorder(TestInfo testInfo) {
    videoRecorder = new VideoRecorderScreenShot(webdriver().object(),
      testInfo.getTestClass().get().getSimpleName(),
      testInfo.getTestMethod().get().getName());
    executor = new ScheduledThreadPoolExecutor(1);
    executor.scheduleAtFixedRate(videoRecorder, 0, 1000, TimeUnit.MILLISECONDS);
  }

  private void stopRecordingAndCloseDriver() {
    videoRecorder.stopRecording();
    videoRecorder.cancel();
    executor.shutdown();
    closeWebDriver();
  }

  private void checkVideoLengthInTime() {
    File videoFile = videoRecorder.getVideoFile();
    assertThat(videoFile.length()).isGreaterThan(0);
    assertThat(videoFile).hasExtension("webm");
  }

}
