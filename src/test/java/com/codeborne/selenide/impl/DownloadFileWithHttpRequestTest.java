package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DownloadFileWithHttpRequestTest {
  DownloadFileWithHttpRequest download = new DownloadFileWithHttpRequest();

  @Test
  void makeAbsoluteUrl() {
    Configuration.baseUrl = "http://test.company.com";

    assertThat(download.makeAbsoluteUrl("/payments/pdf?id=12345"))
      .isEqualTo("http://test.company.com/payments/pdf?id=12345");

    assertThat(download.makeAbsoluteUrl("http://test.company.com/payments/pdf?id=12345"))
      .isEqualTo("http://test.company.com/payments/pdf?id=12345");
  }
}
