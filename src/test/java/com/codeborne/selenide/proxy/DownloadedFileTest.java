package com.codeborne.selenide.proxy;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

final class DownloadedFileTest {
  @Test
  void hasContentDispositionHeader() {
    DownloadedFile file1 = new DownloadedFile(new File("x"), header("content-disposition", "filename=prices.csv"));
    DownloadedFile file2 = new DownloadedFile(new File("x"), header("", ""));
    assertThat(file1.hasContentDispositionHeader()).isTrue();
    assertThat(file2.hasContentDispositionHeader()).isFalse();
  }

  @Test
  void getContentType() {
    DownloadedFile file1 = new DownloadedFile(new File("x"), header("content-type", "application/pdf"));
    DownloadedFile file2 = new DownloadedFile(new File("x"), header("", ""));
    assertThat(file1.getContentType()).isEqualTo("application/pdf");
    assertThat(file2.getContentType()).isNull();
  }

  private Map<String, String> header(String name, String value) {
    Map<String, String> headers = new HashMap<>();
    headers.put(name, value);
    return headers;
  }
}
