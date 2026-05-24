package com.codeborne.selenide.impl;

import com.codeborne.selenide.files.DownloadedFile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class DownloadDetectorTest {
  private final long now = System.currentTimeMillis();
  private final DownloadDetector detector = new DownloadDetector();

  @Test
  void responseWithContentDispositionHeaderHasHighRank() {
    DownloadedFile withHeader = new DownloadedFile(new File("cv.pdf"), 0, 0, withContentDisposition("attachment; filename=cv.pdf"));
    DownloadedFile withoutHeader = new DownloadedFile(new File("script.js"), 0, 0, withContentType("application/javascript"));

    assertThat(sort(withHeader, withoutHeader)).containsExactly(withHeader, withoutHeader);
    assertThat(sort(withoutHeader, withHeader)).containsExactly(withHeader, withoutHeader);
  }

  @Test
  void technicalWebFilesHaveLowRank() {
    DownloadedFile textFile = new DownloadedFile(new File("some-file.txt"), 0, 0, withContentType("application/octet-stream"));
    DownloadedFile htmlFile = new DownloadedFile(new File("index.html"), 0, 0, withContentType("text/html"));
    DownloadedFile jsonFile = new DownloadedFile(new File("event.json"), 0, 0, withContentType("application/json"));

    assertThat(sort(textFile, htmlFile, jsonFile)).containsExactly(textFile, jsonFile, htmlFile);
    assertThat(sort(htmlFile, textFile, jsonFile)).containsExactly(textFile, jsonFile, htmlFile);
    assertThat(sort(htmlFile, jsonFile, textFile)).containsExactly(textFile, jsonFile, htmlFile);
  }

  @Test
  void latestFileWins() {
    DownloadedFile oldest = fileCreatedSecondsAgo("oldest-file.txt", 90);
    DownloadedFile older = fileCreatedSecondsAgo("older-file.txt", 60);
    DownloadedFile younger = fileCreatedSecondsAgo("younger-file.txt", 10);
    DownloadedFile youngest = fileCreatedSecondsAgo("youngest-file.txt", 5);

    assertThat(sort(oldest, older, younger, youngest)).containsExactly(youngest, younger, older, oldest);
    assertThat(sort(older, oldest, younger, youngest)).containsExactly(youngest, younger, older, oldest);
    assertThat(sort(older, younger, oldest, youngest)).containsExactly(youngest, younger, older, oldest);
    assertThat(sort(older, younger, youngest, oldest)).containsExactly(youngest, younger, older, oldest);
    assertThat(sort(youngest, older, younger, oldest)).containsExactly(youngest, younger, older, oldest);
    assertThat(sort(youngest, younger, older, oldest)).containsExactly(youngest, younger, older, oldest);
  }

  @Test
  void finallyJustTakeFirstFileAlphabetically() {
    DownloadedFile first = fileCreatedSecondsAgo("a.txt", 42);
    DownloadedFile second = fileCreatedSecondsAgo("b.txt", 42);
    DownloadedFile third = fileCreatedSecondsAgo("c.txt", 42);

    assertThat(sort(first, second, third)).containsExactly(first, second, third);
    assertThat(sort(second, first, third)).containsExactly(first, second, third);
    assertThat(sort(second, third, first)).containsExactly(first, second, third);
    assertThat(sort(third, second, first)).containsExactly(first, second, third);
  }

  private Map<String, String> withContentDisposition(String contentType) {
    return Map.of("content-disposition", contentType);
  }

  private Map<String, String> withContentType(String contentType) {
    return Map.of("content-type", contentType);
  }

  private DownloadedFile fileCreatedSecondsAgo(String name, int secondsAgo) {
    long lastModified = now - 1000L * secondsAgo;
    File file = mock();
    when(file.getName()).thenReturn(name);
    when(file.lastModified()).thenReturn(lastModified);
    return new DownloadedFile(file, lastModified, 0, emptyMap());
  }

  private List<DownloadedFile> sort(DownloadedFile... files) {
    return Stream.of(files).sorted(detector).toList();
  }
}
