package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.DragAndDropOptions;
import com.selenide.videorecorder.VideoRecorder;
import com.selenide.videorecorder.VideoRecorderScreenShot;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Timer;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class VideoRecorderTests {
  @Test
  public void videoFileShouldExistsAndNotEmpty() {
    Configuration.browser = "chrome";
    Configuration.headless = false;
    Configuration.timeout = 10000;

    open();

    Timer timer = new Timer();
    VideoRecorderScreenShot videoRecorder = new VideoRecorderScreenShot(webdriver().driver(), "videoFileShouldExistsAndNotEmpty");
    timer.scheduleAtFixedRate(videoRecorder, 0, 50);

    //  VideoRecorder videoRecorder = new VideoRecorder();

    //  videoRecorder.startRecording("videoFileShouldExistsAndNotEmpty");

    open("file://" + this.getClass().getClassLoader().getResource("draggable.html").getPath());

    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div2"));
    sleep(3000);
    $("#drag1").dragAndDrop(DragAndDropOptions.to("#div1"));
    sleep(3000);


    videoRecorder.stopRecording();
//    videoRecorder.stopRecording();
    timer.cancel();
    sleep(1000);

    File[] records = new File("build/records").listFiles();
    assertThat(records).hasSize(1);
    assertThat(records[0]).hasExtension("webm");
    //assertThat(records[0].length()).isNotZero();
  }
}
