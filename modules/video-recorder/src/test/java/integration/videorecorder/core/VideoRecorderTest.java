package integration.videorecorder.core;

import com.codeborne.selenide.DragAndDropOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.selenide.videorecorder.core.RecorderFileUtils;
import org.selenide.videorecorder.core.VideoRecorderScreenShot;

import java.nio.file.Path;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.Selenide.webdriver;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.selenide.videorecorder.core.RecorderFileUtils.generateOrGetVideoFolderName;
import static org.selenide.videorecorder.core.RecorderFileUtils.getLastModifiedFile;

public class VideoRecorderTest {
  private VideoRecorderScreenShot videoRecorder;
  private ScheduledThreadPoolExecutor executor;

  @BeforeAll
  public static void beforeAll() {
    open();
  }

  @BeforeEach
  public void beforeEach(TestInfo testInfo) {
    initRecorder(testInfo);
  }

  @AfterEach
  public void afterEach(TestInfo testInfo) {
    stopRecordingAndCloseDriver();
    checkVideoLengthInTime(testInfo);
  }

  @Test
  public void videoFileShouldExistsAndNotEmptyTestCore() {
    open(requireNonNull(getClass().getClassLoader().getResource("draggable.html")));
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div2"));
    sleep(300);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div1"));
    sleep(300);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div2"));
    sleep(300);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div1"));
    sleep(300);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div2"));
    sleep(300);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div1"));
    sleep(300);
  }

  private void initRecorder(TestInfo testInfo) {
    videoRecorder = new VideoRecorderScreenShot(webdriver().object(),
      RecorderFileUtils.generateVideoFileName(testInfo.getTestClass().orElseThrow().getSimpleName(),
        testInfo.getTestMethod().orElseThrow().getName()));
    executor = new ScheduledThreadPoolExecutor(1);
    executor.scheduleAtFixedRate(videoRecorder, 0, 1000, TimeUnit.MILLISECONDS);
  }

  private void stopRecordingAndCloseDriver() {
    videoRecorder.stopRecording();
    executor.shutdown();
    closeWebDriver();
  }

  private void checkVideoLengthInTime(TestInfo testInfo) {
    String testClass = testInfo.getTestClass().orElseThrow().getSimpleName();
    String testMethod = testInfo.getTestMethod().orElseThrow().getName();
    Path videoFolder = generateOrGetVideoFolderName(testClass, testMethod);
    Path videoFile = getLastModifiedFile(videoFolder);
    assertThat(videoFile.toFile().length()).isGreaterThan(0);
    assertThat(videoFile).hasExtension("webm");
  }
}
