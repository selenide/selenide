package integration.videorecorder.core;

import com.codeborne.selenide.DragAndDropOptions;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.selenide.videorecorder.core.VideoRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.selenide.videorecorder.core.RecorderFileUtils.generateVideoFileName;

public class VideoRecorderTest {
  private static final Logger log = LoggerFactory.getLogger(VideoRecorderTest.class);

  @Nullable
  private VideoRecorder videoRecorder;

  @BeforeEach
  public void beforeEach(TestInfo testInfo) {
    Path videoFile = generateVideoFileName(
      testInfo.getTestClass().orElseThrow().getSimpleName(),
      testInfo.getTestMethod().orElseThrow().getName()
    );
    videoRecorder = new VideoRecorder(videoFile);
    videoRecorder.start();
  }

  @AfterEach
  public void afterEach() {
    if (videoRecorder != null) {
      videoRecorder.stop();
      log.info("Video recorded: {}", videoRecorder.videoUrl().orElseThrow());
      checkVideoLengthInTime(videoRecorder.videoFile().orElseThrow());
      videoRecorder = null;
    }
  }

  @Test
  public void videoFileShouldExistsAndNotEmptyTestCore() {
    open(requireNonNull(getClass().getClassLoader().getResource("draggable.html")));
    long start = System.currentTimeMillis();
    for (int i = 0; i < 6; i++) {
      $("#drag1").dragAndDrop(DragAndDropOptions.to("#div2"));
      sleep(1000);
      $("#drag1").dragAndDrop(DragAndDropOptions.to("#div1"));
      sleep(1000);
    }
    open("https://selenide.org");
    sleep(1000);
    open("about:blank");
    long end = System.currentTimeMillis();
    log.info("Finished the experiment in {} ms.", end - start);
  }

  private void checkVideoLengthInTime(Path videoFile) {
    assertThat(videoFile.toFile().length()).isGreaterThan(0);
    assertThat(videoFile).hasExtension("webm");
  }
}
