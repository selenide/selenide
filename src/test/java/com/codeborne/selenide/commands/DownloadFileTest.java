package com.codeborne.selenide.commands;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.files.FileFilters;
import com.codeborne.selenide.impl.DownloadFileToFolder;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.DownloadFileWithProxyServer;
import com.codeborne.selenide.impl.WebElementSource;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.files.FileFilters.none;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

final class DownloadFileTest implements WithAssertions {
  private final SelenideConfig config = new SelenideConfig();
  private final Driver driver = mock(Driver.class);
  private final DownloadFileWithHttpRequest httpget = mock(DownloadFileWithHttpRequest.class);
  private final DownloadFileWithProxyServer proxy = mock(DownloadFileWithProxyServer.class);
  private final DownloadFileToFolder folder = mock(DownloadFileToFolder.class);
  private final DownloadFile command = new DownloadFile(httpget, proxy, folder);
  private final SelenideElement seLink = mock(SelenideElement.class);
  private final WebElementSource linkWithHref = mock(WebElementSource.class);
  private final WebElement link = mock(WebElement.class);
  private final File file = new File("some-file.yxy");

  @BeforeEach
  void setUp() {
    when(driver.config()).thenReturn(config);
    when(linkWithHref.driver()).thenReturn(driver);
    when(linkWithHref.findAndAssertElementIsInteractable()).thenReturn(link);
  }

  @Test
  void canDownloadFile_withHttpGetRequest() throws IOException {
    config.fileDownload(HTTPGET);

    when(httpget.download(any(), any(WebElement.class), anyLong(), any())).thenReturn(file);

    File f = command.execute(seLink, linkWithHref, new Object[]{8000L});

    assertThat(f).isSameAs(file);
    verify(httpget).download(driver, link, 8000L, none());
    verifyNoMoreInteractions(proxy);
  }

  @Test
  void canDownloadFile_withProxyServer() throws IOException {
    config.proxyEnabled(true).fileDownload(PROXY);
    SelenideProxyServer selenideProxy = mock(SelenideProxyServer.class);
    when(linkWithHref.driver()).thenReturn(new DriverStub(config, null, null, selenideProxy));
    when(proxy.download(any(), any(), anyLong(), any())).thenReturn(file);

    File f = command.execute(seLink, linkWithHref, new Object[]{9000L});

    assertThat(f).isSameAs(file);
    verify(proxy).download(linkWithHref, link, 9000L, none());
    verifyNoMoreInteractions(httpget);
  }

  @Test
  void defaultTimeout() {
    config.timeout(3L);

    assertThat(command.getTimeout(config, new Object[0])).isEqualTo(3L);
  }

  @Test
  void customTimeout() {
    config.timeout(3L);

    assertThat(command.getTimeout(config, new Object[]{2L})).isEqualTo(2L);
    assertThat(command.getTimeout(config, new Object[]{2L, none()})).isEqualTo(2L);
  }

  @Test
  void getFileFilterFromArguments() {
    FileFilter expected = FileFilters.withExtension("doc");

    assertThat(command.getFileFilter(new Object[]{})).isSameAs(none());
    assertThat(command.getFileFilter(new Object[]{4000L})).isSameAs(none());
    assertThat(command.getFileFilter(new Object[]{4000L, expected})).isSameAs(expected);
    assertThat(command.getFileFilter(new Object[]{expected})).isSameAs(expected);
  }
}
