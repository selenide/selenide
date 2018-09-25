package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DownloadFileWithHttpRequestTest {
  DownloadFileWithHttpRequest download = new DownloadFileWithHttpRequest();

  @Test
  void makeAbsoluteUrl() {
    Config config = new SelenideConfig().baseUrl("http://test.company.com");
    assertThat(download.makeAbsoluteUrl(config, "/payments/pdf?id=12345"))
      .isEqualTo("http://test.company.com/payments/pdf?id=12345");

    assertThat(download.makeAbsoluteUrl(config, "http://test.company.com/payments/pdf?id=12345"))
      .isEqualTo("http://test.company.com/payments/pdf?id=12345");
  }
}
