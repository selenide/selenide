package com.selenide.videorecorder;

import com.codeborne.selenide.WebDriverRunner;
import org.apache.commons.io.FileUtils;
import org.bytedeco.ffmpeg.ffmpeg;
import org.bytedeco.javacpp.Loader;
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

public class VideoRecorder{
  private static final Logger log = LoggerFactory.getLogger(VideoRecorder.class);

  private DevTools devTools;
  private Process process;
  private OutputStream inputStream;

  private List<String> screens = new LinkedList<>();

  static final String ffmpeg = "%s -f image2pipe -i - -y %s";

  static final String defaultRecordsFolder = "build/records";

  final String ffmpegExecutable = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);

  private String defaultVideoName = new Date().getTime() + "";


  public VideoRecorder() {
    prepareRecordsFolder();
    if (!WebDriverRunner.hasWebDriverStarted()) {
      log.warn("There is no opened browser. We will start one.");
      open();
    }
    devTools = ((HasDevTools) webdriver().object()).getDevTools();
    devTools.createSessionIfThereIsNotOne();
    devTools.send(Page.enable());
  }


  public void startRecording() {
    File recordFileName = new File(defaultRecordsFolder, defaultVideoName + ".webm");
    try {
      process = Runtime.getRuntime().exec(ffmpeg.formatted(ffmpegExecutable, recordFileName.getAbsolutePath()));
      inputStream = process.getOutputStream();

      devTools.addListener(Page.screencastFrame(), frame -> {
        for (int i = 0; i < 15; i++) {
          devTools.send(Page.screencastFrameAck(frame.getSessionId()));
          screens.add(frame.getData());
        }
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    devTools.send(Page.startScreencast(
      Optional.of(Page.StartScreencastFormat.PNG),
      Optional.of(50),
      Optional.empty(),
      Optional.empty(),
      Optional.empty()
    ));
  }

  public void startRecording(String videoName){
    this.defaultVideoName = videoName;
    startRecording();
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
