package integration.videorecorder.core;

import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.selenide.videorecorder.core.Video;
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

public class VideoRecorder2Test {
  private static final Logger log =  LoggerFactory.getLogger(VideoRecorder2Test.class);
  private static final SelenideConfig config = config().browserPosition("700x300").browserSize("800x500");

  private final VideoRecorder videoRecorder = new VideoRecorder();

  @BeforeEach
  public void beforeEach() {
    log.info("before second test");
    videoRecorder.start();
  }

  @Test
  @Video
  public void secondTest() {
    log.info("start second test");
    open(config);
    for (int i = 0; i < 3; i++) {
      open(requireNonNull(getClass().getClassLoader().getResource("search.html")));
      $("[name=q]").type(text("#2 JUnit JUnit JUnit JUnit JUnit JUnit JUnit JUnit JUnit #222")
        .withDelay(ofMillis(5)));
    }
  }

  @AfterEach
  public void checkVideo() {
    log.info("finishing second test");
    videoRecorder.finish();
    log.info("finished second test");
    Path videoFile = getRecordedVideo(currentThread().getId()).orElseThrow();
    assertThat(videoFile).hasExtension("mp4");
    assertThat(videoFile.toFile().length())
      .as(() -> "Video file: " + videoFile)
      .isGreaterThan(0);
  }

  @AfterEach
  void closeBrowser() {
    log.info("closing second browser");
    closeWebDriver();
    log.info("closed second browser");
  }
}
