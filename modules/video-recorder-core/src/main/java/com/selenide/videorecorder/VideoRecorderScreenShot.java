package com.selenide.videorecorder;

import org.apache.commons.lang3.StringUtils;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.bidi.browsingcontext.CaptureScreenshotParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Date;
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
 * @see java.util.TimerTask
 * @see BrowsingContext
 * @see <a href="https://www.selenium.dev/documentation/webdriver/bidirectional/webdriver_bidi/">Selenium BiDi</a>
 */
public class VideoRecorderScreenShot extends TimerTask {
  private static final Logger log = LoggerFactory.getLogger(VideoRecorderScreenShot.class);

  FFmpegFrameRecorder recorder;
  static final String defaultRecordsFolder = "build/records";
  BrowsingContext browsingContext;
  private String defaultVideoName;
  private Path pathToSaveVideo;
  CaptureScreenshotParameters captureScreenshotParameters;

  public VideoRecorderScreenShot(WebDriver webDriver) {
    this(webDriver, null, null);
  }

  public VideoRecorderScreenShot(WebDriver webDriver, String className, String testName) {
    try {
      prepareRecordsFolder();
      createDirectoryToSaveVideoForTest(className, testName);
      defaultVideoName = new Date().getTime() + ".webm";
      initVideoRecordingProcessEx(webDriver);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void createDirectoryToSaveVideoForTest(String className, String testName) throws IOException {
    pathToSaveVideo = Path.of(defaultRecordsFolder);

    if (!StringUtils.isEmpty(testName)) {
      pathToSaveVideo = pathToSaveVideo.resolve(className);
    }
    if (!StringUtils.isEmpty(className)) {
      pathToSaveVideo = pathToSaveVideo.resolve(testName);
    }
    Files.createDirectories(pathToSaveVideo);
  }

  private void initVideoRecordingProcessEx(WebDriver driver) throws IOException, InterruptedException {
    captureScreenshotParameters = new CaptureScreenshotParameters();
    captureScreenshotParameters
      .origin(CaptureScreenshotParameters.Origin.VIEWPORT);
    log.info("Init BiDi");
    browsingContext = new BrowsingContext(driver, driver.getWindowHandle());
    recorder = new FFmpegFrameRecorder(new File(Path.of(pathToSaveVideo.toString(), defaultVideoName).toFile().getAbsolutePath()),
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
    log.info("Video recording");
    try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
      log.info("Make screenshot");
      byte[] lastImage = Base64.getDecoder().decode(browsingContext.captureScreenshot(captureScreenshotParameters));
      Frame convert = converter.getFrame(ImageIO.read(new ByteArrayInputStream(lastImage)), 1.0, true);
      log.info("Write screenshot to stream");
      for (int i = 0; i < 24; i++) {
        recorder.record(convert);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  public void stopRecording(boolean... save) {
    try {
      log.info("Close inputStream");
      recorder.stop();
      Thread.sleep(500);
      if (save.length > 0 && !save[0]) {
        Files.delete(Path.of(pathToSaveVideo.toString(), defaultVideoName));
      }
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }

  }

  private void prepareRecordsFolder() throws IOException {
    log.info("Prepare records folder");
    Files.createDirectories(Path.of(defaultRecordsFolder));
  }

  public File getVideoFile() {
    try {
      return Path.of(pathToSaveVideo.toString(), defaultVideoName).toFile();
    } catch (Exception e) {
      log.error("There was an error getting file!");
      log.error(e.getMessage());
      return null;
    }
  }
}
