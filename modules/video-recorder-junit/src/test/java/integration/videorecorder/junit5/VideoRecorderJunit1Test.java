package integration.videorecorder.junit5;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.selenide.videorecorder.junit5.Video;
import org.selenide.videorecorder.junit5.VideoRecorderExtension;

import java.nio.file.Path;

import static com.codeborne.selenide.Configuration.config;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.TypeOptions.text;
import static java.time.Duration.ofMillis;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

@Video
public class VideoRecorderJunit1Test {
  @Test
  public void firstTest() {
    open(config().browserPosition("50x0"));
    for (int i = 0; i < 3; i++) {
      open(requireNonNull(getClass().getClassLoader().getResource("search.html")));
      $("[name=q]").type(text("#1 JUnit JUnit JUnit JUnit JUnit JUnit JUnit JUnit JUnit #111")
          .withDelay(ofMillis(5)));
    }
  }

  @AfterEach
  public void afterEach() {
    Path path = VideoRecorderExtension.getRecordedVideo().orElseThrow();
    assertThat(path.toFile().length()).isGreaterThan(0);
    assertThat(path.toFile()).hasExtension("webm");
  }
}
