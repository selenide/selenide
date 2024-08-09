package integration;

import com.codeborne.selenide.DragAndDropOptions;
import com.selenide.videorecorder.RecordVideoJunit;
import com.selenide.videorecorder.RecorderFileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.nio.file.Path;
import java.time.Duration;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

@RecordVideoJunit
public class VideoRecorderScreenShotJunitTests {


  @AfterEach
  public void afterEach(TestInfo testInfo) {
    Path path = RecorderFileUtils.generateOrGetVideoFolderName(
      testInfo.getTestClass().get().getSimpleName(),
      testInfo.getTestMethod().get().getName()
    );
    path = RecorderFileUtils.getLastModifiedFile(path);
    assertThat(path.toFile().length()).isGreaterThan(0);
    assertThat(path.toFile()).hasExtension("webm");
  }

  @Test
  public void videoFileShouldExistsAndNotEmptyJunit() {
    open("file://" + this.getClass().getClassLoader().getResource("draggable.html").getPath());
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div2"));
    sleep(3000);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div1"));
    sleep(3000);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div2"));
    sleep(3000);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div1"));
    sleep(3000);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div2"));
    sleep(3000);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div1"));
    sleep(3000);
  }

  @Test
  public void waitingTest() {
    open("https://the-internet.herokuapp.com/dynamic_controls");
    $("#input-example button").click();
    $("#input-example input").shouldBe(enabled, Duration.ofSeconds(10));
  }
}
