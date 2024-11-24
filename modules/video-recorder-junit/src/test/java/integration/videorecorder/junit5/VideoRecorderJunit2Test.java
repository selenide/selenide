package integration.videorecorder.junit5;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.selenide.videorecorder.core.Video;
import org.selenide.videorecorder.junit5.VideoRecorderExtension;

import java.nio.file.Path;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.config;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.TypeOptions.text;
import static integration.videorecorder.junit5.VideoRecorderTester.RE_VIDEO_URL;
import static integration.videorecorder.junit5.VideoRecorderTester.assertionErrors;
import static integration.videorecorder.junit5.VideoRecorderTester.expectAssertionError;
import static java.time.Duration.ofMillis;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VideoRecorderTester.class)
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
    expectAssertionError(() ->
      $("#nope").shouldBe(visible.because("We want this test to fail and save the video"), ofMillis(1))
    );
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

    assertThat(assertionErrors()).hasSize(1);
    assertThat(assertionErrors().get(0)).isInstanceOf(ElementNotFound.class);
    assertThat(assertionErrors().get(0).getMessage()).matches(RE_VIDEO_URL);
  }
}
