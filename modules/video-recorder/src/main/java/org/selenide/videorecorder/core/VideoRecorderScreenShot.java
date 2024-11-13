package org.selenide.videorecorder.core;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.TimerTask;

import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P;

/**
 * Created by Serhii Bryt
 * 07.05.2024 11:57
 */
public class VideoRecorderScreenShot extends TimerTask {
  private static final Logger log = LoggerFactory.getLogger(VideoRecorderScreenShot.class);

  private FFmpegFrameRecorder recorder;
  private final WebDriver driver;

  public VideoRecorderScreenShot(WebDriver driver, File videoFile) {
    this.driver = driver;
    try {
      initVideoRecordingProcess(videoFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public VideoRecorderScreenShot(WebDriver driver, Path videoFile) {
    this(driver, videoFile.toFile());
  }

  private void initVideoRecordingProcess(File videoFile) throws IOException {
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
