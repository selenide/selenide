package integration.videorecorder.junit5;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.selenide.videorecorder.core.Video;
import org.selenide.videorecorder.junit5.VideoRecorderExtension;

import java.nio.file.Path;

import static com.codeborne.selenide.Configuration.config;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.TypeOptions.text;
import static java.time.Duration.ofMillis;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MarkFailedTestsAsPassed.class)
class VideoRecorderJunit2Test {
  @Test
  @Video
  void secondTest() {
    open(config().browserPosition("200x300").browserSize("800x500"));
    for (int i = 0; i < 3; i++) {
      open(requireNonNull(getClass().getClassLoader().getResource("search.html")));
      $("[name=q]").type(text("#2 JUnit JUnit JUnit JUnit JUnit JUnit JUnit JUnit JUnit #222")
        .withDelay(ofMillis(5)));
    }
  }

  @AfterEach
  void closeBrowser() {
    closeWebDriver();
  }

  @AfterEach
  void afterEach() {
    Path path = VideoRecorderExtension.getRecordedVideo().orElseThrow();
    assertThat(path.toFile().length()).isGreaterThan(0);
    assertThat(path.toFile()).hasExtension("webm");
  }
}
