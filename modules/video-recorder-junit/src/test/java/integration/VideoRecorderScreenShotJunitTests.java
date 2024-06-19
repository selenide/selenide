package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.DragAndDropOptions;
import com.google.common.collect.Iterables;
import com.selenide.videorecorder.RecordVideoJunit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.MutableCapabilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

@RecordVideoJunit
public class VideoRecorderScreenShotJunitTests {

  @BeforeAll
  public static void setUp() {
    MutableCapabilities mutableCapabilities = new MutableCapabilities();
    mutableCapabilities.setCapability("webSocketUrl", true);
    Configuration.browserCapabilities = mutableCapabilities;
    open();
  }

  @AfterEach
  public void afterEach(TestInfo testInfo) throws IOException {
    Path path = Path.of("build/records",
      testInfo.getTestClass().get().getSimpleName(),
      testInfo.getTestMethod().get().getName());
    Path last = Iterables.getLast(Files.list(path).toList());
    assertThat(last.toFile().length()).isGreaterThan(0);
    assertThat(last.toFile()).hasExtension("webm");
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
}
