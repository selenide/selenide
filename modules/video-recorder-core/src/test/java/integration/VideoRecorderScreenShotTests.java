package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.DragAndDropOptions;
import com.google.common.collect.Iterables;
import com.selenide.videorecorder.VideoRecorderScreenShot;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class VideoRecorderScreenShotTests {
  VideoRecorderScreenShot videoRecorder;
  ScheduledThreadPoolExecutor executor;


  @AfterEach
  public void afterEach() throws IOException {
    stopRecordingAndCloseDriver();
    checkVideoLengthInTime();
  }

  @Test
  public void videoFileShouldExistsAndNotEmptyChromeTest(TestInfo testInfo) {
    initChrome();
    open();
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

  @Test
  public void videoFileShouldExistsAndNotEmptyFFTest(TestInfo testInfo) {
    initFirefox();
    open();
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

  private void initChrome() {
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.setCapability("webSocketUrl", true);
    Configuration.browserCapabilities = chromeOptions;
    Configuration.browser = "chrome";
    Configuration.headless = true;
  }

  private void initFirefox() {
    FirefoxOptions firefoxOptions = new FirefoxOptions();
    firefoxOptions.setCapability("webSocketUrl", true);
    Configuration.browserCapabilities = firefoxOptions;
    Configuration.browser = "firefox";
    Configuration.headless = true;
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
