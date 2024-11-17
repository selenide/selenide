package integration.videorecorder.testng;

import com.codeborne.selenide.TypeOptions;
import org.selenide.videorecorder.testng.VideoRecorderListener;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.nio.file.Path;

import static com.codeborne.selenide.Configuration.config;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static java.time.Duration.ofMillis;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

@Listeners(VideoRecorderListener.class)
public class VideoRecorderTestNg2Test {

  @AfterMethod
  public void afterMethod() {
    Path path = VideoRecorderListener.getRecordedVideo().orElseThrow();
    assertThat(path.toFile().length()).isGreaterThan(0);
    assertThat(path.toFile()).hasExtension("webm");
  }

  @Test
  public void secondTest() {
    open(config().browserPosition("300x500"));
    for (int i = 0; i < 3; i++) {
      open(requireNonNull(getClass().getClassLoader().getResource("search.html")));
      $("[name=q]").type(TypeOptions.text("#2 TestNG TestNG TestNG TestNG TestNG TestNG TestNG #222")
        .withDelay(ofMillis(5)));
    }
  }
}
