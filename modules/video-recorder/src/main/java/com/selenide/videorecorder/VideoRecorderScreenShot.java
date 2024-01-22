package com.selenide.videorecorder;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebDriverRunner;
import org.apache.commons.io.FileUtils;
import org.bytedeco.javacpp.Loader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v120.page.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static com.codeborne.selenide.Selenide.*;

public class VideoRecorderScreenShot extends TimerTask {
  private static final Logger log = LoggerFactory.getLogger(VideoRecorderScreenShot.class);

  private DevTools devTools;
  private Process process;
  private OutputStream inputStream;

  private List<String> screens = new LinkedList<>();

  static final String ffmpeg = "%s -f image2pipe -i - -y %s";

  static final String defaultRecordsFolder = "build/records";

  final String ffmpegExecutable = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);

  private String defaultVideoName = new Date().getTime() + "";

  public VideoRecorderScreenShot(Driver driver, String fileName) {
    prepareRecordsFolder();
//    if (!driver.hasWebDriverStarted()) {
//      log.warn("There is no opened browser. We will start one.");
//      driver.open();
//    }

    devTools = ((HasDevTools) driver.getWebDriver()).getDevTools();
    devTools.createSessionIfThereIsNotOne();
    devTools.send(Page.enable());
    File recordFileName = new File(defaultRecordsFolder, fileName != null ? fileName : defaultVideoName + ".webm");
    try {
      process = Runtime.getRuntime().exec(ffmpeg.formatted(ffmpegExecutable, recordFileName.getAbsolutePath()));
      inputStream = process.getOutputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public VideoRecorderScreenShot(Driver driver) {
    this(driver, null);
  }


  @Override
  public void run() {
    String send = devTools.send(Page.captureScreenshot(
      Optional.of(Page.CaptureScreenshotFormat.PNG),
      Optional.empty(),
      Optional.empty(),
      Optional.empty(),
      Optional.empty(),
      Optional.of(true)
    ));
    screens.add(send);
  }

  public void stopRecording() {
    try {
      for (String screen : screens) {
        inputStream.write(Base64.getDecoder().decode(screen));
      }
      inputStream.flush();
      inputStream.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void prepareRecordsFolder() {
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
