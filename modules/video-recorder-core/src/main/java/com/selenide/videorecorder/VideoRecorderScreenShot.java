package com.selenide.videorecorder;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.TimerTask;

import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P;

/*
  Created by Serhii Bryt
  07.05.2024 11:57
 */

/**
 * This is video recording based on taking screenshots. How it works.
 * Webdriver instance should exist. Then we initialize {@link BrowsingContext} from Selenium BiDi.
 * From one hand Selenium BiDi allows us to use common api for all (ar almost all) browsers.
 * From other hand BiDi uses WebSocket connection so this functionality can work in parallel with the main Selenium.
 * After that we initialize ffmpeg process.
 * During the recording process we call run() method every second, take screenshot and write it 24 times
 * to the ffmpeg process output stream. At the emd we save file with [timestamp].webm format.
 * Files are stored in build/records folder of the project by default.
 *
 * @see TimerTask
 * @see BrowsingContext
 * @see <a href="https://www.selenium.dev/documentation/webdriver/bidirectional/webdriver_bidi/">Selenium BiDi</a>
 */
public class VideoRecorderScreenShot extends TimerTask {
  private static final Logger log = LoggerFactory.getLogger(VideoRecorderScreenShot.class);

  private FFmpegFrameRecorder recorder;
  private WebDriver driver;

  public VideoRecorderScreenShot(WebDriver driver, File videoFile) {
    try {
      this.driver = driver;
      initVideoRecordingProcessEx(videoFile);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public VideoRecorderScreenShot(WebDriver driver, Path videoFile) {
    this(driver, videoFile.toFile());
  }

  private void initVideoRecordingProcessEx(File videoFile) throws IOException, InterruptedException {
    recorder = new FFmpegFrameRecorder(videoFile,
      driver.manage().window().getSize().getWidth(),
      driver.manage().window().getSize().getHeight());
    recorder.setFormat("webm");
    recorder.setVideoCodecName("libx264");
    recorder.setVideoOption("crf", "24");
    recorder.setPixelFormat(AV_PIX_FMT_YUV420P);
    recorder.setFrameRate(24);
    recorder.start();
  }


  @Override
  public void run() {
    log.debug("Video recording");
    try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
      log.debug("Make screenshot");
      byte[] lastImage = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
      Frame convert = converter.getFrame(ImageIO.read(new ByteArrayInputStream(lastImage)), 1.0, true);
      log.debug("Write screenshot to stream");
      for (int i = 0; i < 24; i++) {
        recorder.record(convert);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  public void stopRecording() {
    log.debug("Close inputStream");
    try {
      recorder.stop();
      this.cancel();
    } catch (FFmpegFrameRecorder.Exception e) {
      throw new RuntimeException(e);
    }
  }
}
