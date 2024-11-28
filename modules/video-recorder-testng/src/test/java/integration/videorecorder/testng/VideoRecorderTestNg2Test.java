package integration.videorecorder.testng;

import com.codeborne.selenide.ex.ElementNotFound;
import org.selenide.videorecorder.core.Video;
import org.selenide.videorecorder.testng.VideoRecorderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.nio.file.Path;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.config;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.TypeOptions.text;
import static integration.videorecorder.testng.VideoRecorderTester.RE_VIDEO_URL;
import static integration.videorecorder.testng.VideoRecorderTester.assertionErrors;
import static integration.videorecorder.testng.VideoRecorderTester.expectAssertionError;
import static java.time.Duration.ofMillis;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

@Listeners(VideoRecorderTester.class)
public class VideoRecorderTestNg2Test {

  private static final Logger log = LoggerFactory.getLogger(VideoRecorderTestNg2Test.class);

  @Test
  @Video
  public void secondTest() {
    open(config().browserPosition("200x300").browserSize("800x500"));
    for (int i = 0; i < 3; i++) {
      open(requireNonNull(getClass().getClassLoader().getResource("search.html")));
      $("[name=q]").type(text("#2 TestNG TestNG TestNG TestNG TestNG TestNG TestNG #222")
        .withDelay(ofMillis(5)));
    }
    expectAssertionError(() ->
      $("#nope").shouldBe(visible.because("We want this test to fail and save the video"), ofMillis(1))
    );
  }

  @AfterMethod(alwaysRun = true)
  final void assertVideoHasBeenSaved(ITestResult result) {
    Path path = VideoRecorderListener.getRecordedVideo().orElseThrow();
    assertThat(path.toFile().length()).isGreaterThan(0);
    assertThat(path.toFile()).hasExtension("webm");

    assertThat(assertionErrors()).hasSize(1);
    assertThat(assertionErrors().get(0)).isInstanceOf(ElementNotFound.class);
    assertThat(assertionErrors().get(0).getMessage()).matches(RE_VIDEO_URL);
  }

  @AfterMethod(alwaysRun = true)
  final void closeBrowser() {
    closeWebDriver();
  }
}
