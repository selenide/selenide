package com.selenide.videorecorder;

import org.apache.commons.io.FileUtils;
import org.bytedeco.javacpp.Loader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v124.page.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class VideoRecorder extends Thread {
  private static final Logger log = LoggerFactory.getLogger(VideoRecorder.class);

  private DevTools devTools;
  private Process process;
  private OutputStream inputStream;
  static final String ffmpeg = "%s -loglevel error -f image2pipe -avioflags direct -fpsprobesize 0 -probesize 32 -analyzeduration 0 -c:v mjpeg -i - -y -an -r 25 -c:v vp8 -qmin 0 -qmax 50 -crf 8 -deadline realtime -speed 8 -b:v 1M -threads 1  %s";

  static final String defaultRecordsFolder = "build/records";
  final String ffmpegExecutable = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);

  private AtomicReference<Long> prev_timestamp = new AtomicReference<>();

  private byte[] lastImage;

  private File recordFile;


  public VideoRecorder(WebDriver driver) {
    //prepareRecordsFolder();
//    if (!WebDriverRunner.hasWebDriverStarted()) {
//      log.warn("Webdriver is not started yet!!! So we will start it!");
//      open();
//    }
    devTools = ((HasDevTools) driver).getDevTools();
    devTools.createSessionIfThereIsNotOne();
    devTools.send(Page.enable());
  }


  public void startRecording(String fileName) throws IOException {
    recordFile = new File(defaultRecordsFolder, fileName + ".webm");
    process = Runtime.getRuntime().exec(ffmpeg.formatted(ffmpegExecutable, recordFile.getAbsolutePath()));
    inputStream = process.getOutputStream();
    //   inputStream = (ByteArrayOutputStream) process.getOutputStream();
    prev_timestamp.set(new Date().getTime() / 1000);
    devTools.addListener(Page.screencastFrame(), frame -> {
      devTools.send(Page.screencastFrameAck(frame.getSessionId()));
      long current_timestamp = frame.getMetadata().getTimestamp().get().toJson().longValue();
      log.info("Current timestamp: {}", current_timestamp);
      lastImage = Base64.getDecoder().decode(frame.getData());
      log.info("Prev timestamp: {}", prev_timestamp.get());
      long duration = current_timestamp - prev_timestamp.get();
      log.info("Duration: {}", duration);
      prev_timestamp.set(current_timestamp);
      log.info("Prev timestamp: {}", prev_timestamp.get());
      long repeatCount = Math.max(1, Math.round(25 * duration));
      log.info("Repeat count: {}\n\n", repeatCount);

      for (long i = 0; i < repeatCount; i++) {
        try {
          inputStream.write(lastImage);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });

    devTools.send(Page.startScreencast(
      Optional.of(Page.StartScreencastFormat.JPEG),
      Optional.of(50),
      Optional.empty(),
      Optional.empty(),
      Optional.empty()
    ));
  }

  public void startRecording() {
    try {
      startRecording(new Date().getTime() + "");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void stopRecording() throws IOException {
    devTools.send(Page.stopScreencast());
    inputStream.write(lastImage);
    inputStream.flush();
    inputStream.close();
    //inputStream.reset();
  }

  public File getRecordFile() {
    return recordFile;
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

  @Override
  public void run() {
    super.run();
    startRecording();
  }
}
