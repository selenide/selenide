package com.codeborne.selenide.commands;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Context;
import com.codeborne.selenide.ContextStub;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.DownloadFileWithProxyServer;
import com.codeborne.selenide.impl.WebElementSource;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Configuration.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.Configuration.FileDownloadMode.PROXY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class DownloadFileTest implements WithAssertions {
  private DownloadFileWithHttpRequest httpget = mock(DownloadFileWithHttpRequest.class);
  private DownloadFileWithProxyServer proxy = mock(DownloadFileWithProxyServer.class);
  private DownloadFile command = new DownloadFile(httpget, proxy);
  private WebElementSource linkWithHref = mock(WebElementSource.class);
  private WebElement link = mock(WebElement.class);
  private File file = new File("some-file.yxy");

  @BeforeEach
  @AfterEach
  void reset() {
    Configuration.proxyEnabled = false;
    Configuration.fileDownload = HTTPGET;
  }

  @BeforeEach
  void setUp() {
    when(linkWithHref.findAndAssertElementIsVisible()).thenReturn(link);
  }

  @Test
  void canDownloadFile_withHttpGetRequest() throws IOException {
    Configuration.proxyEnabled = false;
    Configuration.fileDownload = HTTPGET;
    Context context = new ContextStub(null, null, null);
    when(linkWithHref.context()).thenReturn(context);
    when(httpget.download(any(), any(WebElement.class), anyLong())).thenReturn(file);

    File f = command.execute(null, linkWithHref, new Object[]{8000L});

    assertThat(f).isSameAs(file);
    verify(httpget).download(context, link, 8000L);
    verifyNoMoreInteractions(proxy);
  }

  @Test
  void canDownloadFile_withProxyServer() throws IOException {
    Configuration.proxyEnabled = true;
    Configuration.fileDownload = PROXY;
    SelenideProxyServer selenideProxy = mock(SelenideProxyServer.class);
    when(linkWithHref.context()).thenReturn(new ContextStub(null, null, selenideProxy));
    when(proxy.download(any(), any(), any(), anyLong())).thenReturn(file);

    File f = command.execute(null, linkWithHref, new Object[]{9000L});

    assertThat(f).isSameAs(file);
    verify(proxy).download(linkWithHref, link, selenideProxy, 9000L);
    verifyNoMoreInteractions(httpget);
  }

  @Test
  void proxyServerShouldBeEnabled() {
    Configuration.proxyEnabled = false;
    Configuration.fileDownload = PROXY;

    assertThatThrownBy(() -> command.execute(null, linkWithHref, new Object[0]))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("Cannot download file: proxy server is not enabled");
  }

  @Test
  void proxyServerShouldBeStarted() {
    Configuration.proxyEnabled = true;
    Configuration.fileDownload = PROXY;
    when(linkWithHref.context()).thenReturn(new ContextStub(mock(Browser.class), mock(WebDriver.class), null));

    assertThatThrownBy(() -> command.execute(null, linkWithHref, new Object[0]))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("Cannot download file: proxy server is not started");
  }

  @Test
  void defaultTimeout() {
    Configuration.timeout = 3L;

    assertThat(command.getTimeout(new Object[0])).isEqualTo(3L);
  }

  @Test
  void customerTimeout() {
    Configuration.timeout = 3L;

    assertThat(command.getTimeout(new Object[]{2L})).isEqualTo(2L);
  }
}
