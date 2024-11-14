package integration.videorecorder.junit5;

import com.codeborne.selenide.DragAndDropOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.selenide.videorecorder.junit5.Video;
import org.selenide.videorecorder.junit5.VideoRecorderExtension;

import java.nio.file.Path;
import java.time.Duration;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

@Video
public class VideoRecorderJunitTest {
  @AfterEach
  public void afterEach(TestInfo testInfo) {
    Path path = VideoRecorderExtension.getRecordedVideo().orElseThrow();
    String expectedVideoFolder = "video/%s/%s".formatted(
      testInfo.getTestClass().orElseThrow().getSimpleName(),
      testInfo.getTestMethod().orElseThrow().getName()
    );
    assertThat(path.toFile().length()).isGreaterThan(0);
    assertThat(path.toFile()).hasExtension("webm");
    assertThat(path.getParent().toString()).contains(expectedVideoFolder);
  }

  @Test
  public void videoFileShouldExistsAndNotEmptyJunit() {
    open(requireNonNull(getClass().getClassLoader().getResource("draggable.html")));
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div2"));
    sleep(600);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div1"));
    sleep(600);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div2"));
    sleep(600);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div1"));
    sleep(600);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div2"));
    sleep(600);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div1"));
//    sleep(600);
//    $("#zopa").shouldBe(visible);
  }

  @Test
  public void waitingTest() {
    open("https://the-internet.herokuapp.com/dynamic_controls");
    $("#input-example button").click();
    $("#input-example input").shouldBe(enabled, Duration.ofSeconds(10));
//    $("#siski").shouldBe(visible);
  }
}
