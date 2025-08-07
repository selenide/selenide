package integration.videorecorder.core;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.junit5.TextReportExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
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

@ExtendWith(TextReportExtension.class)
public class VideoRecorder3Test {
  private static final Logger log =  LoggerFactory.getLogger(VideoRecorder3Test.class);
  private static final SelenideConfig config = config().browserPosition("500x600").browserSize("800x500");
  private final VideoRecorder videoRecorder = new VideoRecorder();

  @BeforeEach
  public void beforeEach() {
    Configuration.webdriverLogsEnabled = true;
    currentThread().setName("video-test-3-%s".formatted(videoRecorder.videoId()));
    log.info("before third test");
    videoRecorder.start();
  }

  @RepeatedTest(20)
  public void thirdTest() {
    log.info("start third test");
    open(config);
    for (int i = 0; i < 3; i++) {
      open(requireNonNull(getClass().getClassLoader().getResource("search.html")));
      $("[name=q]").type(text("#3 JUnit JUnit JUnit JUnit JUnit JUnit JUnit JUnit JUnit #333")
        .withDelay(ofMillis(5)));
    }
  }

  @AfterEach
  public void checkVideo() {
    log.info("finishing third test");
    videoRecorder.finish();
    log.info("finish third test");
    Path videoFile = getRecordedVideo(currentThread().getId()).orElseThrow(() -> new AssertionError("video file not found in thread " + currentThread()));
    assertThat(videoFile).as(() -> "video file not found in thread " + currentThread()).exists();
    assertThat(videoFile).hasExtension("mp4");
    assertThat(videoFile.toFile().length())
      .as(() -> "Video file: " + videoFile)
      .isGreaterThan(0);
  }

  @AfterEach
  void closeBrowser() {
    log.info("closing third browser");
    closeWebDriver();
    log.info("closed third browser");
  }
}
