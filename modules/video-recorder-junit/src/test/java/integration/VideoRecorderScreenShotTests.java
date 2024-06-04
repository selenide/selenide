package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.DragAndDropOptions;
import com.selenide.videorecorder.DisableVideoRecording;
import com.selenide.videorecorder.RecordVideoJunit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.codeborne.selenide.Selenide.*;

@RecordVideoJunit
public class VideoRecorderScreenShotTests {

  private static final Logger log = LoggerFactory.getLogger(VideoRecorderScreenShotTests.class);

  @BeforeAll
  public static void setUp() {
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.setCapability("webSocketUrl", true);
    Configuration.browserCapabilities = chromeOptions;
    //Configuration.browser = "firefox";
    Configuration.headless = false;
    Configuration.timeout = 10000;
  }

  @Test
  public void videoFileShouldExistsAndNotEmpty() throws IOException {
    long now = System.currentTimeMillis();

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
    log.debug("Time for test: " + (System.currentTimeMillis() - now) / 1000);
  }

  @Test
  public void videoFileShouldExistsAndNotEmpty1() {
    long now = System.currentTimeMillis();
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
    log.debug("Time for test: " + (System.currentTimeMillis() - now) / 1000);
  }


}
