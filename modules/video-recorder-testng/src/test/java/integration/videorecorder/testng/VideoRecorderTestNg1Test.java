package integration.videorecorder.testng;

import org.selenide.videorecorder.core.Video;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Configuration.config;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.TypeOptions.text;
import static java.time.Duration.ofMillis;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.selenide.videorecorder.testng.VideoRecorderListener.getRecordedVideo;

@Listeners(MarkFailedTestsAsPassed.class)
public class VideoRecorderTestNg1Test {

  @Test
  @Video
  public void firstTest() {
    open(config().browserPosition("50x50").browserSize("800x500"));
    for (int i = 0; i < 3; i++) {
      open(requireNonNull(getClass().getClassLoader().getResource("search.html")));
      $("[name=q]").type(text("#1 TestNG TestNG TestNG TestNG TestNG TestNG TestNG #111")
        .withDelay(ofMillis(5)));
    }
  }

  @AfterMethod
  final void checkVideo() {
    assertThat(getRecordedVideo())
      .as("Test is annotated with @Video, but did not fail -> no video")
      .isEmpty();
  }

  @AfterMethod
  final void closeBrowser() {
    closeWebDriver();
  }
}
