package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideConfig;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.util.HashSet;

import static java.util.Collections.singletonList;
import static org.apache.hc.client5.http.protocol.HttpClientContext.COOKIE_STORE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

final class DownloadFileWithHttpRequestTest {
  private final DownloadFileWithHttpRequest download = new DownloadFileWithHttpRequest(
    new Downloader(new DummyRandomizer("111-222-333-444"))
  );

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

  @Test
  void shouldNotAddCookieIfBrowserIsNotOpened() {
    Driver driver = mock(Driver.class);

    HttpContext httpContext = download.createHttpContext(driver);

    assertThat(httpContext.getAttribute(COOKIE_STORE)).isNull();
  }

  @Test
  void shouldAddAllCookiesFromOpenedBrowser() {
    WebDriver webDriver = mock(WebDriver.class, RETURNS_DEEP_STUBS);
    when(webDriver.manage().getCookies()).thenReturn(new HashSet<>(singletonList(new Cookie("jsessionid", "123456789"))));
    Driver driver = mock(Driver.class);
    when(driver.hasWebDriverStarted()).thenReturn(true);
    when(driver.getWebDriver()).thenReturn(webDriver);

    HttpContext httpContext = download.createHttpContext(driver);

    BasicCookieStore bs = (BasicCookieStore) httpContext.getAttribute(COOKIE_STORE);
    assertThat(bs.getCookies()).hasSize(1);
    assertThat(bs.getCookies().get(0).getName()).isEqualTo("jsessionid");
    assertThat(bs.getCookies().get(0).getValue()).isEqualTo("123456789");
  }

  @Test
  void getFileName_fromHttpHeader() {
    Header header = new BasicHeader("Content-Disposition", "Content-Disposition=attachment; filename=image.jpeg");
    HttpResponse response = responseWithHeaders(header);

    assertThat(download.getFileName("/blah.jpg", response)).isEqualTo("image.jpeg");
  }

  @Test
  void getFileName_fromUrl() {
    HttpResponse response = responseWithHeaders();

    assertThat(download.getFileName("/blah.jpg", response)).isEqualTo("blah.jpg");
  }

  @Test
  void getFileName_random() {
    HttpResponse response = responseWithHeaders();

    assertThat(download.getFileName("/images/6584836/", response)).isEqualTo("111-222-333-444");
  }

  private HttpResponse responseWithHeaders(Header... headers) {
    HttpResponse response = mock(HttpResponse.class);
    when(response.getHeaders()).thenReturn(headers);
    return response;
  }
}
