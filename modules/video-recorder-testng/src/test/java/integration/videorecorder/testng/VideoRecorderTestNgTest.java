package integration.videorecorder.testng;

import com.codeborne.selenide.DragAndDropOptions;
import org.selenide.videorecorder.core.RecorderFileUtils;
import org.selenide.videorecorder.testng.VideoRecorderListener;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.time.Duration;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

@Listeners(VideoRecorderListener.class)
public class VideoRecorderTestNgTest {

  @AfterMethod
  public void afterMethod(ITestResult result) {
    Path path = RecorderFileUtils
      .generateOrGetVideoFolderName(result.getTestClass().getRealClass().getSimpleName(),
        result.getMethod().getMethodName());
    path = RecorderFileUtils.getLastModifiedFile(path);
    assertThat(path.toFile().length()).isGreaterThan(0);
    assertThat(path.toFile()).hasExtension("webm");
  }

  @Test
  public void videoFileShouldExistsAndNotEmptyTestTestNg() {
    open(requireNonNull(getClass().getClassLoader().getResource("draggable.html")));
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div2"));
    sleep(300);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div1"));
    sleep(300);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div2"));
    sleep(300);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div1"));
    sleep(300);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div2"));
    sleep(300);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div1"));
    sleep(300);
  }

  @Test
  public void waitingTest() {
    open("https://the-internet.herokuapp.com/dynamic_controls");
    $("#input-example button").click();
    $("#input-example input").shouldBe(enabled, Duration.ofSeconds(10));
  }
}
