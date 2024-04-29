package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.DragAndDropOptions;
import com.selenide.videorecorder.VideoRecorder;
import com.selenide.videorecorder.VideoRecorderScreenShot;
import org.junit.jupiter.api.*;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Timer;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class VideoRecorderTests {

  private static final Logger log = LoggerFactory.getLogger(VideoRecorderTests.class);

  @BeforeAll
  public static void setUp() {
    FirefoxOptions firefoxOptions = new FirefoxOptions();
    firefoxOptions.setCapability("webSocketUrl", true);
    Configuration.browserCapabilities = firefoxOptions;
    Configuration.browser = "firefox";
    Configuration.headless = false;
    Configuration.timeout = 10000;
  }

  static VideoRecorderScreenShot videoRecorder = null;
  static Timer timer  = new Timer();

  @BeforeEach
  public void beforeEach() {
    open();
    videoRecorder = new VideoRecorderScreenShot(webdriver().object());
    timer.schedule(videoRecorder, 0, 1000);
  }

  @AfterEach
  public void afterEach() throws IOException {
    videoRecorder.cancel();
    //timer.cancel();
    closeWebDriver();
  }

  @AfterAll
  public static void tearDown(){
    timer.cancel();
  }

  @Test
  public void videoFileShouldExistsAndNotEmpty() throws IOException {

//    open();
//    VideoRecorder videoRecorder = new VideoRecorder(webdriver().object());
////    Thread thread = new Thread(videoRecorder);
////    VideoRecorder videoRecorder = new VideoRecorder(webdriver().object());
////    videoRecorder.startRecording();
//   // videoRecorder.setDaemon(true);
//    videoRecorder.start();

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

    now = System.currentTimeMillis();
    log.debug("Time for record dump: " + (System.currentTimeMillis() - now) / 1000);
    sleep(1000);
//    videoRecorder.stopRecording();
//    File recordFile = videoRecorder.getRecordFile();
//    videoRecorder.interrupt();
//    assertThat(recordFile).hasExtension("webm");
//    assertThat(recordFile.length()).isGreaterThan(0);
  }

  @Test
  public void videoFileShouldExistsAndNotEmpty1() {

//    open();

//    VideoRecorder videoRecorder = new VideoRecorder(webdriver().object());
    //videoRecorder.setDaemon(true);
    //   Thread thread = new Thread(videoRecorder);
//    videoRecorder.start();

    // videoRecorder.startRecording();

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

    now = System.currentTimeMillis();
    log.debug("Time for record dump: " + (System.currentTimeMillis() - now) / 1000);
    sleep(1000);
//    File recordFile = videoRecorder.getRecordFile();
//    videoRecorder.stopRecording();
//    videoRecorder.interrupt();
//    assertThat(recordFile).hasExtension("webm");
//    assertThat(recordFile.length()).isGreaterThan(0);
  }


}
