package integration.videorecorder.core;

import com.codeborne.selenide.DragAndDropOptions;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.selenide.videorecorder.core.VideoRecorder;

import java.nio.file.Path;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.Selenide.webdriver;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.selenide.videorecorder.core.RecorderFileUtils.generateOrGetVideoFolderName;
import static org.selenide.videorecorder.core.RecorderFileUtils.generateVideoFileName;
import static org.selenide.videorecorder.core.RecorderFileUtils.getLastModifiedFile;

public class VideoRecorderTest {
  @Nullable
  private VideoRecorder videoRecorder;
  @Nullable
  private ScheduledThreadPoolExecutor executor;

  @BeforeEach
  public void beforeEach(TestInfo testInfo) {
    Path videoFile = generateVideoFileName(
      testInfo.getTestClass().orElseThrow().getSimpleName(),
      testInfo.getTestMethod().orElseThrow().getName()
    );
    videoRecorder = new VideoRecorder(webdriver().object(), videoFile);
    executor = new ScheduledThreadPoolExecutor(1);
    executor.scheduleAtFixedRate(videoRecorder, 0, 1000, TimeUnit.MILLISECONDS);
  }

  @AfterEach
  public void afterEach(TestInfo testInfo) {
    if (videoRecorder != null) videoRecorder.stopRecording();
    if (executor != null) executor.shutdown();
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

  private void checkVideoLengthInTime(TestInfo testInfo) {
    String testClass = testInfo.getTestClass().orElseThrow().getSimpleName();
    String testMethod = testInfo.getTestMethod().orElseThrow().getName();
    Path videoFolder = generateOrGetVideoFolderName(testClass, testMethod);
    Path videoFile = getLastModifiedFile(videoFolder);
    assertThat(videoFile.toFile().length()).isGreaterThan(0);
    assertThat(videoFile).hasExtension("webm");
  }
}
