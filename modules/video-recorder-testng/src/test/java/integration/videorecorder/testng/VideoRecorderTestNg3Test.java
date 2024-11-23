package integration.videorecorder.testng;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Condition.visible;
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
public class VideoRecorderTestNg3Test {

  @Test
  public void thirdTest() {
    open(config().browserPosition("50x600").browserSize("800x500"));
    for (int i = 0; i < 3; i++) {
      open(requireNonNull(getClass().getClassLoader().getResource("search.html")));
      $("[name=q]").type(text("#3 TestNG TestNG TestNG TestNG TestNG TestNG TestNG #333")
        .withDelay(ofMillis(5)));
    }
    $("#nope").shouldBe(visible.because("We want this test to fail, but video will not be saved."), ofMillis(1));
  }

  @AfterMethod
  final void assertNoVideoHasBeenSaved() {
    assertThat(getRecordedVideo())
      .as("Test failed, but it's not annotated with @Video -> no video")
      .isEmpty();
  }

  @AfterMethod
  final void closeBrowser() {
    closeWebDriver();
  }
}
