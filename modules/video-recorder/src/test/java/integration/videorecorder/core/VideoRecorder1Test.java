package integration.videorecorder.core;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.selenide.videorecorder.core.VideoRecorder;

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
  @Nullable
  private VideoRecorder videoRecorder;

  @BeforeEach
  public void beforeEach() {
    videoRecorder = new VideoRecorder();
    videoRecorder.start();
  }

  @Test
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
    requireNonNull(videoRecorder).stop();
    Path videoFile = getRecordedVideo(currentThread().getId()).orElseThrow();
    assertThat(videoFile.toFile().length()).isGreaterThan(0);
    assertThat(videoFile).hasExtension("webm");
  }

  @AfterEach
  void closeBrowser() {
    closeWebDriver();
  }
}
