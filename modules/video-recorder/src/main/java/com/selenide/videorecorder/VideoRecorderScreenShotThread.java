package com.selenide.videorecorder;

import org.apache.commons.io.FileUtils;
import org.bytedeco.javacpp.Loader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.BiDiException;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Date;
import java.util.TimerTask;

public class VideoRecorderScreenShotThread extends Thread{
  int counter = 0;
  private static final Logger log = LoggerFactory.getLogger(VideoRecorderScreenShotThread.class);

  static final String ffmpeg = "%s -loglevel error -f image2pipe -i - -y -an -r 25 -b:v 1M -threads 1 %s";

  static final String defaultRecordsFolder = "build/records";

  BrowsingContext browsingContext;

  final String ffmpegExecutable = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);

  private String defaultVideoName;

  private static Process recordProcess;
  private static OutputStream outputStream;

  //CaptureScreenshotParameters captureScreenshotParameters;

  WebDriver driver;

  public VideoRecorderScreenShotThread(WebDriver webDriver, String... videoName) {
    if (videoName.length > 0 && videoName != null && !videoName[0].isEmpty()) {
      defaultVideoName = videoName[0];
    } else {
      defaultVideoName = new Date().getTime() + "";
    }
//    captureScreenshotParameters = new CaptureScreenshotParameters();
//    captureScreenshotParameters.imageFormat("image/jpeg", 0.7)
//      .origin(CaptureScreenshotParameters.Origin.VIEWPORT);
    log.info("Init devtools");
    browsingContext = new BrowsingContext(webDriver, webDriver.getWindowHandle());
    File recordFileName = new File(defaultRecordsFolder, defaultVideoName + ".webm");
    try {
      recordProcess = Runtime.getRuntime().exec(ffmpeg.formatted(ffmpegExecutable, recordFileName.getAbsolutePath()));
      outputStream = recordProcess.getOutputStream();
      Thread.sleep(1000);
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void doWork(){
    run();
  }

  @Override
  public void run() {
    log.info("Video recording");
    try {
      log.info("Make screenshot");
      byte[] lastImage = Base64.getDecoder().decode(browsingContext.captureScreenshot().getBytes());
      log.info("Write screenshot to stream");
      for (int i = 0; i < 15; i++) {
          outputStream.write(lastImage);
      }
    } catch (BiDiException e) {
      log.error("Cannot make screenshot!!!");
      log.error(e.getMessage());
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  public void stopRecording(boolean... save) {
    try {
      log.info("Close inputStream");
      outputStream.flush();
      outputStream.close();
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
