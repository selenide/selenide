package com.codeborne.selenide.impl;

import com.codeborne.selenide.proxy.DownloadedFile;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

import java.io.File;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class DownloadDetectorTest {
  private final long now = System.currentTimeMillis();
  private final DownloadDetector detector = new DownloadDetector();

  @Test
  void responseWithContentDispositionHeaderHasHighRank() {
    DownloadedFile response1 = new DownloadedFile(new File("cv.pdf"), withContentDisposition("attachment; filename=cv.pdf"));
    DownloadedFile response2 = new DownloadedFile(new File("script.js"), withContentType("application/javascript"));

    assertThat(detector.compare(response1, response2)).isEqualTo(-1);
    assertThat(detector.compare(response2, response1)).isEqualTo(1);
  }

  @Test
  void htmlResponseHasLowRank() {
    DownloadedFile response1 = new DownloadedFile(new File("some-file.txt"), withContentType("application/octet-stream"));
    DownloadedFile response2 = new DownloadedFile(new File("event.json"), withContentType("application/json"));

    assertThat(detector.compare(response1, response2)).isEqualTo(-1);
    assertThat(detector.compare(response2, response1)).isEqualTo(1);
  }

  @Test
  void latestFileWins() {
    DownloadedFile response1 = new DownloadedFile(fileCreatedSecondsAgo("earlier-file.txt", 60), emptyMap());
    DownloadedFile response2 = new DownloadedFile(fileCreatedSecondsAgo("latest-file.txt", 10), emptyMap());

    assertThat(detector.compare(response1, response2)).isEqualTo(-1);
    assertThat(detector.compare(response2, response1)).isEqualTo(1);
  }

  @Test
  void finallyJustTakeFirstFileAlphabetically() {
    DownloadedFile response1 = new DownloadedFile(fileCreatedSecondsAgo("a.txt", 42), emptyMap());
    DownloadedFile response2 = new DownloadedFile(fileCreatedSecondsAgo("b.txt", 42), emptyMap());

    assertThat(detector.compare(response1, response2)).isEqualTo(-1);
    assertThat(detector.compare(response2, response1)).isEqualTo(1);
  }

  private Map<String, String> withContentDisposition(String contentType) {
    Map<String, String> map = new HashMap<>();
    map.put("content-disposition", contentType);
    return map;
  }

  private Map<String, String> withContentType(String contentType) {
    Map<String, String> map = new HashMap<>();
    map.put("content-type", contentType);
    return map;
  }

  private File fileCreatedSecondsAgo(String name, int secondsAgo) {
    File file = mock(File.class);
    when(file.getName()).thenReturn(name);
    when(file.lastModified()).thenReturn(now - 1000L * secondsAgo);
    return file;
  }
}
