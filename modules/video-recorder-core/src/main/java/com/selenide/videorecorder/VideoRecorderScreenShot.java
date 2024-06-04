package com.selenide.videorecorder;

import org.apache.commons.io.FileUtils;
import org.bytedeco.javacpp.Loader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.BiDiException;
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
import java.util.*;

public class VideoRecorderScreenShot extends TimerTask {
  int counter = 0;
  private static final Logger log = LoggerFactory.getLogger(VideoRecorderScreenShot.class);

  static final String ffmpeg = "%s -loglevel error -f image2pipe -avioflags direct -fpsprobesize 0 -probesize 32 -analyzeduration 0 -c:v mjpeg -i - -y -an -r 24 -c:v vp8 -qmin 0 -qmax 50 -crf 8 -deadline realtime -speed 8 -b:v 1M -threads 1 %s";

  static final String defaultRecordsFolder = "build/records";

  BrowsingContext browsingContext;

  final String ffmpegExecutable = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);

  private String defaultVideoName;

  private static Process recordProcess;
  //private static OutputStream outputStream;

  CaptureScreenshotParameters captureScreenshotParameters;

  public VideoRecorderScreenShot(WebDriver webDriver, String... videoName) {
    if (videoName.length > 0 && videoName != null && !videoName[0].isEmpty()) {
      defaultVideoName = videoName[0];
    } else {
      defaultVideoName = new Date().getTime() + "";
    }
    captureScreenshotParameters = new CaptureScreenshotParameters();
    captureScreenshotParameters
      //.imageFormat("image/jpeg", 0.5)
      .origin(CaptureScreenshotParameters.Origin.VIEWPORT);
    log.info("Init devtools");
    browsingContext = new BrowsingContext(webDriver, webDriver.getWindowHandle());
    File recordFileName = new File(defaultRecordsFolder, defaultVideoName + ".webm");
    try {
      recordProcess = Runtime.getRuntime().exec(ffmpeg.formatted(ffmpegExecutable, recordFileName.getAbsolutePath()));
     // outputStream = recordProcess.getOutputStream();
      Thread.sleep(1000);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public void run() {
    log.info("Video recording");
    try {
      log.info("Make screenshot");
      byte[] lastImage = Base64.getDecoder().decode(browsingContext.captureScreenshot(captureScreenshotParameters));
      BufferedImage read = ImageIO.read(new ByteArrayInputStream(lastImage));

      BufferedImage newBufferedImage = new BufferedImage(
        read.getWidth(), // Returns the width of the BufferedImage.
        read.getHeight(),  // Returns the height of the BufferedImage.
        BufferedImage.TYPE_INT_BGR);

      newBufferedImage.createGraphics()
        .drawImage(read, 0, 0, Color.white, null);

      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      ImageIO.write(newBufferedImage, "jpg", byteArrayOutputStream);
      log.info("Write screenshot to stream");
      for (int i = 0; i < 24; i++) {
        recordProcess.getOutputStream().write(byteArrayOutputStream.toByteArray());
       // outputStream.write(byteArrayOutputStream.toByteArray());
      }
    } catch (BiDiException e) {
      log.error("Cannot make screenshot!!!");
      log.error(e.getMessage());
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  public void stopRecording(boolean... save) {
    try {
      log.info("Close inputStream");
      recordProcess.getOutputStream().flush();
      recordProcess.getOutputStream().close();
      recordProcess.destroy();
      //outputStream.flush();
      //outputStream.close();
      if (save.length > 0 && !save[0]) {
        Files.delete(Path.of(defaultRecordsFolder, defaultVideoName));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  private void prepareRecordsFolder() {
    log.info("Prepare records folder");
    File recordsFolder = new File(defaultRecordsFolder);
    if (!recordsFolder.exists()) {
      recordsFolder.mkdir();
    } else {
      try {
        FileUtils.cleanDirectory(recordsFolder);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

}
