package com.codeborne.selenide.commands;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.DownloadFileWithProxyServer;
import com.codeborne.selenide.impl.WebElementSource;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class DownloadFileTest implements WithAssertions {
  private Config config = mock(Config.class);
  private Driver driver = mock(Driver.class);
  private DownloadFileWithHttpRequest httpget = mock(DownloadFileWithHttpRequest.class);
  private DownloadFileWithProxyServer proxy = mock(DownloadFileWithProxyServer.class);
  private DownloadFile command = new DownloadFile(httpget, proxy);
  private WebElementSource linkWithHref = mock(WebElementSource.class);
  private WebElement link = mock(WebElement.class);
  private File file = new File("some-file.yxy");

  @BeforeEach
  void setUp() {
    when(driver.config()).thenReturn(config);
    when(linkWithHref.driver()).thenReturn(driver);
    when(linkWithHref.findAndAssertElementIsInteractable()).thenReturn(link);
  }

  @Test
  void canDownloadFile_withHttpGetRequest() throws IOException {
    when(config.proxyEnabled()).thenReturn(false);
    when(config.fileDownload()).thenReturn(HTTPGET);

    when(httpget.download(any(), any(WebElement.class), anyLong())).thenReturn(file);

    File f = command.execute(null, linkWithHref, new Object[]{8000L});

    assertThat(f).isSameAs(file);
    verify(httpget).download(driver, link, 8000L);
    verifyNoMoreInteractions(proxy);
  }

  @Test
  void canDownloadFile_withProxyServer() throws IOException {
    SelenideConfig config = new SelenideConfig().proxyEnabled(true).fileDownload(PROXY);
    SelenideProxyServer selenideProxy = mock(SelenideProxyServer.class);
    when(linkWithHref.driver()).thenReturn(new DriverStub(config, null, null, selenideProxy));
    when(proxy.download(any(), any(), any(), anyLong())).thenReturn(file);

    File f = command.execute(null, linkWithHref, new Object[]{9000L});

    assertThat(f).isSameAs(file);
    verify(proxy).download(linkWithHref, link, selenideProxy, 9000L);
    verifyNoMoreInteractions(httpget);
  }

  @Test
  void proxyServerShouldBeEnabled() {
    when(config.proxyEnabled()).thenReturn(false);
    when(config.fileDownload()).thenReturn(PROXY);

    assertThatThrownBy(() -> command.execute(null, linkWithHref, new Object[0]))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("Cannot download file: proxy server is not enabled");
  }

  @Test
  void proxyServerShouldBeStarted() {
    SelenideConfig config = new SelenideConfig().proxyEnabled(true).fileDownload(PROXY);
    when(linkWithHref.driver()).thenReturn(new DriverStub(config, mock(Browser.class), mock(WebDriver.class), null));

    assertThatThrownBy(() -> command.execute(null, linkWithHref, new Object[0]))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("Cannot download file: proxy server is not started");
  }

  @Test
  void defaultTimeout() {
    SelenideConfig config = new SelenideConfig().timeout(3L);

    assertThat(command.getTimeout(config, new Object[0])).isEqualTo(3L);
  }

  @Test
  void customerTimeout() {
    SelenideConfig config = new SelenideConfig().timeout(3L);

    assertThat(command.getTimeout(config, new Object[]{2L})).isEqualTo(2L);
  }
}
