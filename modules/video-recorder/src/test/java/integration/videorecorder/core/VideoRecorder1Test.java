package integration.videorecorder.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.selenide.videorecorder.core.VideoRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import static com.codeborne.selenide.Configuration.config;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.TypeOptions.text;
import static java.lang.Thread.currentThread;
import static java.time.Duration.ofMillis;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.selenide.videorecorder.core.RecordedVideos.getRecordedVideo;

public class VideoRecorder1Test {
  private static final Logger log =  LoggerFactory.getLogger(VideoRecorder1Test.class);

  private final VideoRecorder videoRecorder = new VideoRecorder();

  @BeforeEach
  public void beforeEach() {
    log.info("before first test");
    videoRecorder.start();
  }

  @RepeatedTest(10)
  public void firstTest() {
    open(config().browserPosition("50x5").browserSize("800x500"));
    for (int i = 0; i < 3; i++) {
      open(requireNonNull(getClass().getClassLoader().getResource("search.html")));
      $("[name=q]").type(text("#1 JUnit JUnit JUnit JUnit JUnit JUnit JUnit JUnit JUnit #111")
        .withDelay(ofMillis(5)));
    }
  }

  @AfterEach
  public void checkVideo() {
    log.info("finishing first test");
    videoRecorder.finish();
    log.info("finished first test");
    Path videoFile = getRecordedVideo(currentThread().getId()).orElseThrow();
    assertThat(videoFile).hasExtension("mp4");
    assertThat(videoFile.toFile().length())
      .as(() -> "Video file: " + videoFile)
      .isGreaterThan(0);
  }

  @AfterEach
  void closeBrowser() {
    log.info("closing first browser");
    closeWebDriver();
    log.info("closed first browser");
  }
}
