package org.selenide.videorecorder.core;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.SelenideErrorFormatter;
import com.codeborne.selenide.impl.Screenshot;

import java.nio.file.Path;
import java.util.Optional;

import static com.codeborne.selenide.ex.Strings.join;
import static java.lang.Thread.currentThread;

public class ErrorFormatterWithVideoUrl extends SelenideErrorFormatter {
  @Override
  public String generateErrorDetails(AssertionError error, Driver driver, Screenshot screenshot, long timeoutMs) {
    return join(screenshot.summary(), videoUrl(), timeout(timeoutMs), causedBy(error.getCause()));
  }

  protected String videoUrl() {
    Optional<Path> recordedVideo = RecordedVideos.getRecordedVideo(currentThread().getId());
    return recordedVideo
      .map(videoFile -> String.format("Video: %s", videoFile.toUri()))
      .orElse("");
  }
}
