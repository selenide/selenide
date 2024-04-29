package com.selenide.videorecorder;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebDriverRunner;
import org.apache.commons.io.FileUtils;
import org.bytedeco.javacpp.Loader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.bidi.browsingcontext.CaptureScreenshotParameters;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v123.page.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.codeborne.selenide.Selenide.*;

public class VideoRecorderScreenShot extends TimerTask {
  private static final Logger log = LoggerFactory.getLogger(VideoRecorderScreenShot.class);

  private List<String> screens = new LinkedList<>();

  static final String ffmpeg = "%s -loglevel error -f image2pipe -avioflags direct -fpsprobesize 0 -probesize 32 -analyzeduration 0 -c:v mjpeg -i - -y -an -r 25 -c:v vp8 -qmin 0 -qmax 50 -crf 8 -deadline realtime -speed 8 -b:v 1M -threads 1  %s";

  static final String defaultRecordsFolder = "build/records";

  BrowsingContext browsingContext;

  //final String ffmpegExecutable = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);

  private String defaultVideoName;

  private AtomicBoolean stop = new AtomicBoolean(false);

  private Process recordProcess;
  private OutputStream outputStream;

  private AtomicReference<Long> prev_timestamp = new AtomicReference<>();

  private byte[] lastImage;

  public VideoRecorderScreenShot(WebDriver webDriver) {
   // prepareRecordsFolder();
    defaultVideoName = new Date().getTime() + "";
    log.info("Init devtools");
    browsingContext = new BrowsingContext(webDriver,
      webDriver.getWindowHandle());
    File recordFileName = new File(defaultRecordsFolder, defaultVideoName + ".webm");
    String ffmpegExecutable = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
    try {
      recordProcess = Runtime.getRuntime().exec(ffmpeg.formatted(ffmpegExecutable, recordFileName.getAbsolutePath()));
      outputStream = recordProcess.getOutputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public void run() {
    //recordVideoProcess();
    log.info("Video recording");
    //prev_timestamp.set(System.currentTimeMillis() / 1000);
    CaptureScreenshotParameters captureScreenshotParameters = new CaptureScreenshotParameters();
    captureScreenshotParameters
      .imageFormat("image/png")
      .origin(CaptureScreenshotParameters.Origin.VIEWPORT);
    log.info("Make screenshot");
    String image = browsingContext.captureScreenshot(captureScreenshotParameters);
    long current_timestamp = System.currentTimeMillis();
    prev_timestamp.set(current_timestamp);
    long duration = current_timestamp - prev_timestamp.get();
    lastImage = Base64.getDecoder().decode(image.getBytes());
    long repeatCount = Math.max(1, Math.round(25 * duration));
    for (long i = 0; i < repeatCount; i++) {
      try {
        outputStream.write(lastImage);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

  }

  private void recordVideoProcess() {
    log.info("Video recording");
    prev_timestamp.set(System.currentTimeMillis() / 1000);
    while (!stop.get()) {
      CaptureScreenshotParameters captureScreenshotParameters = new CaptureScreenshotParameters();
      captureScreenshotParameters
        .imageFormat("image/png")
        .origin(CaptureScreenshotParameters.Origin.VIEWPORT);
      log.info("Make screenshot");
      String image = browsingContext.captureScreenshot(captureScreenshotParameters);
      long current_timestamp = System.currentTimeMillis();
      long duration = current_timestamp - prev_timestamp.get();
      prev_timestamp.set(current_timestamp);
      lastImage = Base64.getDecoder().decode(image);
      long repeatCount = Math.max(1, Math.round(25 * duration));
      for (long i = 0; i < repeatCount; i++) {
        try {
          outputStream.write(lastImage);
        } catch (IOException e) {
          //throw new RuntimeException(e);
        }
      }
    }
  }

  @Override
  public boolean cancel() {
    stopRecording();
    return super.cancel();
  }

  public void stopRecording() {
    try {
      log.info("Close inputStream");
      //stop.set(true);
    //  Thread.sleep(2000);
      outputStream.write(lastImage);
      outputStream.flush();
      outputStream.close();
      //recordProcess.destroy();
      //interrupt();
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
