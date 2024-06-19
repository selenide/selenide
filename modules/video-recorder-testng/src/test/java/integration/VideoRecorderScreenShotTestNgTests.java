package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.DragAndDropOptions;
import com.google.common.collect.Iterables;
import com.selenide.videorecorder.BrowserRecorderListener;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

@Listeners(BrowserRecorderListener.class)
public class VideoRecorderScreenShotTestNgTests {

  @BeforeClass
  public void setUp() {
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.setCapability("webSocketUrl", true);
    Configuration.browserCapabilities = chromeOptions;
    Configuration.headless = true;
    Configuration.timeout = 10000;
  }

  @AfterMethod
  public void afterMethod(ITestResult result) throws IOException {
    Path path = Path.of("build/records",
      result.getTestClass().getRealClass().getSimpleName(),
      result.getMethod().getMethodName());
    Path last = Iterables.getLast(Files.list(path).toList());
    assertThat(last.toFile().length()).isGreaterThan(0);
    assertThat(last.toFile()).hasExtension("webm");
  }


  @Test
  public void videoFileShouldExistsAndNotEmptyTest() {
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
