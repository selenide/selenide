package integration.videorecorder.core;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.junit5.TextReportExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.selenide.videorecorder.core.VideoRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;

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

@ExtendWith(TextReportExtension.class)
public class VideoRecorder1Test {
  private static final Logger log = LoggerFactory.getLogger(VideoRecorder1Test.class);
  private static final SelenideConfig config = config().browserPosition("50x5").browserSize("800x500");

  private final VideoRecorder videoRecorder = new VideoRecorder();

  @BeforeEach
  public void beforeEach() {
    Configuration.webdriverLogsEnabled = true;
    currentThread().setName("video-test-1-%s".formatted(videoRecorder.videoId()));
    log.info("before first test");
    videoRecorder.start();
  }

  @Test
  public void firstTest() {
    open(config);
    for (int i = 0; i < 3; i++) {
      open(requireNonNull(getClass().getClassLoader().getResource("search.html")));
      $("[name=q]").type(text("#1 JUnit JUnit JUnit JUnit JUnit JUnit JUnit JUnit JUnit #111")
        .withDelay(ofMillis(5)));
    }
  }

  @AfterEach
  public void checkVideo() {
    log.info("finishing first test");
    Optional<Path> result = videoRecorder.finish();
    log.info("finished first test");

    Path videoFile = getRecordedVideo(currentThread().getId())
      .orElseThrow(() -> new AssertionError("video file not found in thread " + currentThread()));
    assertThat(result).contains(videoFile);
    assertThat(videoFile).as(() -> "video file not found in thread " + currentThread()).exists();
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
