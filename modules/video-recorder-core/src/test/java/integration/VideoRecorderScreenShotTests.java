package integration;

import com.codeborne.selenide.DragAndDropOptions;
import com.selenide.videorecorder.RecorderFileUtils;
import com.selenide.videorecorder.VideoRecorderScreenShot;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class VideoRecorderScreenShotTests {
  VideoRecorderScreenShot videoRecorder;
  ScheduledThreadPoolExecutor executor;


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
      RecorderFileUtils.generateVideoFileName(testInfo.getTestClass().get().getSimpleName(),
        testInfo.getTestMethod().get().getName()));
    executor = new ScheduledThreadPoolExecutor(1);
    executor.scheduleAtFixedRate(videoRecorder, 0, 1000, TimeUnit.MILLISECONDS);
  }

  private void stopRecordingAndCloseDriver() {
    videoRecorder.stopRecording();
    executor.shutdown();
    closeWebDriver();
  }

  private void checkVideoLengthInTime(TestInfo testInfo) {
    Path videoFile = RecorderFileUtils.getLastModifiedFile(RecorderFileUtils
      .generateOrGetVideoFolderName(testInfo.getTestClass().get().getSimpleName(), testInfo.getTestMethod().get().getName()));
    assertThat(videoFile.toFile().length()).isGreaterThan(0);
    assertThat(videoFile).hasExtension("webm");
  }

}
