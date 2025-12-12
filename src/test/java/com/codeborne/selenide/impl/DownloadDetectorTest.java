package com.codeborne.selenide.impl;

import com.codeborne.selenide.files.DownloadedFile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class DownloadDetectorTest {
  private final long now = System.currentTimeMillis();
  private final DownloadDetector detector = new DownloadDetector();

  @Test
  void responseWithContentDispositionHeaderHasHighRank() {
    DownloadedFile response1 = new DownloadedFile(new File("cv.pdf"), 0, 0, withContentDisposition("attachment; filename=cv.pdf"));
    DownloadedFile response2 = new DownloadedFile(new File("script.js"), 0, 0, withContentType("application/javascript"));

    assertThat(detector.compare(response1, response2)).isEqualTo(-1);
    assertThat(detector.compare(response2, response1)).isEqualTo(1);
  }

  @Test
  void htmlResponseHasLowRank() {
    DownloadedFile response1 = new DownloadedFile(new File("some-file.txt"), 0, 0, withContentType("application/octet-stream"));
    DownloadedFile response2 = new DownloadedFile(new File("event.json"), 0, 0, withContentType("application/json"));

    assertThat(detector.compare(response1, response2)).isEqualTo(-1);
    assertThat(detector.compare(response2, response1)).isEqualTo(1);
  }

  @Test
  void latestFileWins() {
    DownloadedFile response1 = new DownloadedFile(fileCreatedSecondsAgo("earlier-file.txt", 60), 0, 0, emptyMap());
    DownloadedFile response2 = new DownloadedFile(fileCreatedSecondsAgo("latest-file.txt", 10), 0, 0, emptyMap());

    assertThat(detector.compare(response1, response2)).isEqualTo(-1);
    assertThat(detector.compare(response2, response1)).isEqualTo(1);
  }

  @Test
  void finallyJustTakeFirstFileAlphabetically() {
    DownloadedFile response1 = new DownloadedFile(fileCreatedSecondsAgo("a.txt", 42), 0, 0, emptyMap());
    DownloadedFile response2 = new DownloadedFile(fileCreatedSecondsAgo("b.txt", 42), 0, 0, emptyMap());

    assertThat(detector.compare(response1, response2)).isEqualTo(-1);
    assertThat(detector.compare(response2, response1)).isEqualTo(1);
  }

  private Map<String, String> withContentDisposition(String contentType) {
    return Map.of("content-disposition", contentType);
  }

  private Map<String, String> withContentType(String contentType) {
    return Map.of("content-type", contentType);
  }

  private File fileCreatedSecondsAgo(String name, int secondsAgo) {
    File file = mock();
    when(file.getName()).thenReturn(name);
    when(file.lastModified()).thenReturn(now - 1000L * secondsAgo);
    return file;
  }
}
