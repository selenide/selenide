package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideConfig;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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

  @Test
  void addsUserAgentWhenDownloadingFile() {
    Driver driver = mock(Driver.class);
    HttpGet httpGet = mock(HttpGet.class);
    when(driver.hasWebDriverStarted()).thenReturn(true);
    when(driver.getUserAgent()).thenReturn("This is Chrome, baby");

    download.addHttpHeaders(driver, httpGet);

    verify(httpGet).setHeader("User-Agent", "This is Chrome, baby");
  }

  @Test
  void doesNotAddUserAgentIfBrowserNotStarted() {
    Driver driver = mock(Driver.class);
    HttpGet httpGet = mock(HttpGet.class);

    download.addHttpHeaders(driver, httpGet);

    verifyNoMoreInteractions(httpGet);
  }
}
