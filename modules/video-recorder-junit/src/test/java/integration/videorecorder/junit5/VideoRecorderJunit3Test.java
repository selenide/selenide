package integration.videorecorder.junit5;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.selenide.videorecorder.junit5.VideoRecorderExtension;

import static com.codeborne.selenide.Configuration.config;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.TypeOptions.text;
import static java.time.Duration.ofMillis;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MarkFailedTestsAsPassed.class)
class VideoRecorderJunit3Test {
  @Test
  void thirdTest() {
    open(config().browserPosition("50x600").browserSize("800x500"));
    for (int i = 0; i < 3; i++) {
      open(requireNonNull(getClass().getClassLoader().getResource("search.html")));
      $("[name=q]").type(text("#3 JUnit JUnit JUnit JUnit JUnit JUnit JUnit JUnit JUnit #333")
        .withDelay(ofMillis(5)));
    }
  }

  @AfterEach
  void closeBrowser() {
    closeWebDriver();
  }

  @AfterEach
  void afterEach() {
    assertThat(VideoRecorderExtension.getRecordedVideo())
      .as("Test failed, but it's not annotated with @Video -> no video")
      .isEmpty();
  }
}
