package com.selenide.videorecorder;

import org.apache.commons.io.FileUtils;
import org.bytedeco.javacpp.Loader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.BiDiException;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.bidi.browsingcontext.CaptureScreenshotParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class VideoRecorderScreenShot extends TimerTask {
  private static final Logger log = LoggerFactory.getLogger(VideoRecorderScreenShot.class);

  static final String ffmpeg = "%s -loglevel error -f image2pipe -avioflags direct -fpsprobesize 0 -probesize 32 -analyzeduration 0 -c:v mjpeg -i - -y -an -r 24 -c:v vp8 -qmin 0 -qmax 50 -crf 8 -deadline realtime -speed 8 -b:v 1M -threads 1 %s";

  static final String defaultRecordsFolder = "build/records";

  BrowsingContext browsingContext;

  final String ffmpegExecutable = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);

  private String defaultVideoName;

  private Process recordProcess;
  private OutputStream outputStream;

  CaptureScreenshotParameters captureScreenshotParameters;

  // private byte[] lastImage;

  public VideoRecorderScreenShot(WebDriver webDriver) {
    captureScreenshotParameters = new CaptureScreenshotParameters();
    captureScreenshotParameters
      .imageFormat("image/jpeg")
      .origin(CaptureScreenshotParameters.Origin.VIEWPORT);
    defaultVideoName = new Date().getTime() + "";
    log.info("Init devtools");
    browsingContext = new BrowsingContext(webDriver, webDriver.getWindowHandle());
    File recordFileName = new File(defaultRecordsFolder, defaultVideoName + ".webm");
    try {
      recordProcess = Runtime.getRuntime().exec(ffmpeg.formatted(ffmpegExecutable, recordFileName.getAbsolutePath()));
      outputStream = recordProcess.getOutputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public void run() {
    log.info("Video recording");
    log.info("Make screenshot");
    String image;
    try {
      image = browsingContext.captureScreenshot(captureScreenshotParameters);
    } catch (BiDiException ignore) {
      return;
    }
    byte[] lastImage = Base64.getDecoder().decode(image.getBytes());
    for (int i = 0; i < 25; i++) {
      try {
        outputStream.write(lastImage);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

  }

  public void stopRecording() {
    try {
      log.info("Close inputStream");
      outputStream.flush();
      outputStream.close();
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
