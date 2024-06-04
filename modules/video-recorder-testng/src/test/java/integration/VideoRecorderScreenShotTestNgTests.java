package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.DragAndDropOptions;
import com.selenide.videorecorder.BrowserRecorderListener;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.codeborne.selenide.Selenide.*;

@Listeners(BrowserRecorderListener.class)
public class VideoRecorderScreenShotTestNgTests {

  private static final Logger log = LoggerFactory.getLogger(VideoRecorderScreenShotTestNgTests.class);

  @BeforeClass
  public static void setUp() {
//    FirefoxOptions chromeOptions = new FirefoxOptions();
//    chromeOptions.setCapability("webSocketUrl", true);
//    chromeOptions.setLogLevel(FirefoxDriverLogLevel.TRACE);
//    Configuration.browserCapabilities = chromeOptions;
    //Configuration.browser = FirefoxProvider.class.getName();
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.setCapability("webSocketUrl", true);
    Configuration.browserCapabilities = chromeOptions;
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
