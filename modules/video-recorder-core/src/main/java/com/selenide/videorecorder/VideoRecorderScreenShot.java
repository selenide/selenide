package com.selenide.videorecorder;

import org.apache.commons.lang3.StringUtils;
import org.bytedeco.javacpp.Loader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.bidi.browsingcontext.CaptureScreenshotParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Date;
import java.util.TimerTask;

/**
 * Created by Serhii Bryt
 * 07.05.2024 11:57
 **/

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

  static final String ffmpeg = "%s -loglevel error -f image2pipe -avioflags direct -fpsprobesize 0 -probesize 32 -analyzeduration 0 -c:v mjpeg -i - -y -an -r 24 -c:v vp8 -qmin 0 -qmax 50 -crf 8 -deadline realtime -speed 8 -b:v 1M -threads 1 %s";

  static final String defaultRecordsFolder = "build/records";

  BrowsingContext browsingContext;

  final String ffmpegExecutable = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);

  private String defaultVideoName;

  private Process recordProcess;
  private OutputStream outputStream;
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
      initVideoRecordingProcess(webDriver);
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

  private void initVideoRecordingProcess(WebDriver driver) throws IOException, InterruptedException {
    captureScreenshotParameters = new CaptureScreenshotParameters();
    captureScreenshotParameters
      .origin(CaptureScreenshotParameters.Origin.VIEWPORT)
      .imageFormat("image/png", 0.5);
    log.debug("Init BiDi");
    browsingContext = new BrowsingContext(driver, driver.getWindowHandle());
    recordProcess = Runtime.getRuntime().exec(ffmpeg.formatted(ffmpegExecutable,
      Path.of(pathToSaveVideo.toString(), defaultVideoName).toFile().getAbsolutePath()));
    outputStream = recordProcess.getOutputStream();
    Thread.sleep(1000);
  }


  @Override
  public void run() {
    log.debug("Video recording");
    try {
      log.debug("Make screenshot");
      byte[] lastImage = Base64.getDecoder().decode(browsingContext.captureScreenshot(captureScreenshotParameters));
      byte[] convertedImage = convertPngToJpeg(lastImage);
      log.debug("Write screenshot to stream");
      for (int i = 0; i < 24; i++) {
        outputStream.write(convertedImage);
      }
    } catch (Exception e) {
      log.debug(e.getMessage());
    }
  }

  private byte[] convertPngToJpeg(byte[] pngAsByteArray) {
    try {
      BufferedImage read = ImageIO.read(new ByteArrayInputStream(pngAsByteArray));
      BufferedImage newBufferedImage = new BufferedImage(
        read.getWidth(), // Returns the width of the BufferedImage.
        read.getHeight(),  // Returns the height of the BufferedImage.
        BufferedImage.TYPE_INT_BGR);
      newBufferedImage.createGraphics()
        .drawImage(read, 0, 0, Color.white, null);
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      ImageIO.write(newBufferedImage, "jpg", byteArrayOutputStream);
      return byteArrayOutputStream.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public void stopRecording(boolean... save) {
    try {
      log.debug("Close inputStream");
      outputStream.flush();
      outputStream.close();
      Thread.sleep(500);
      recordProcess.destroy();
      if (save.length > 0 && !save[0]) {
        Files.delete(Path.of(pathToSaveVideo.toString(), defaultVideoName));
      }
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }

  }

  private void prepareRecordsFolder() throws IOException {
    log.debug("Prepare records folder");
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
