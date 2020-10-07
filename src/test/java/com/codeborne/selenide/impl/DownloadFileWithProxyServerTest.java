package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.proxy.DownloadedFile;
import com.codeborne.selenide.proxy.FileDownloadFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.stream.Stream;

import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.files.FileFilters.none;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class DownloadFileWithProxyServerTest implements WithAssertions {
  private final Waiter waiter = new DummyWaiter();
  private final WindowsCloser windowsCloser = spy(new DummyWindowsCloser());
  private final DownloadFileWithProxyServer command = new DownloadFileWithProxyServer(waiter, windowsCloser);
  private final SelenideConfig config = new SelenideConfig();
  private final WebDriver webdriver = mock(WebDriver.class);
  private final SelenideProxyServer proxy = mock(SelenideProxyServer.class);
  private final WebElementSource linkWithHref = mock(WebElementSource.class);
  private final WebElement link = mock(WebElement.class);
  private final FileDownloadFilter filter = spy(new FileDownloadFilter(config));

  @BeforeEach
  void setUp() {
    config.proxyEnabled(true);
    config.fileDownload(PROXY);
    when(proxy.responseFilter("download")).thenReturn(filter);
    when(linkWithHref.driver()).thenReturn(new DriverStub(config, new Browser("opera", false), webdriver, proxy));
    when(linkWithHref.findAndAssertElementIsInteractable()).thenReturn(link);
    when(linkWithHref.toString()).thenReturn("<a href='report.pdf'>report</a>");
  }

  @Test
  void canInterceptFileViaProxyServer() throws IOException {
    emulateServerResponseWithFiles(new File("report.pdf"));

    File file = command.download(linkWithHref, link, 3000, none());

    assertThat(file.getName()).isEqualTo("report.pdf");
    verify(filter).activate();
    verify(link).click();
    verify(filter).deactivate();
  }

  @Test
  void closesNewWindowIfFileWasOpenedInSeparateWindow() throws IOException {
    emulateServerResponseWithFiles(new File("report.pdf"));

    File file = command.download(linkWithHref, link, 3000, none());

    assertThat(file.getName()).isEqualTo("report.pdf");
    verify(windowsCloser).runAndCloseArisedWindows(same(webdriver), any());
  }

  @Test
  void throwsFileNotFoundExceptionIfNoFilesHaveBeenDownloadedAfterClick() {
    emulateServerResponseWithFiles();

    assertThatThrownBy(() -> command.download(linkWithHref, link, 3000, none()))
      .isInstanceOf(FileNotFoundException.class)
      .hasMessageStartingWith("Failed to download file <a href='report.pdf'>report</a>");
  }

  @Test
  void proxyServerShouldBeEnabled() {
    config.proxyEnabled(false);
    config.fileDownload(PROXY);

    assertThatThrownBy(() -> command.download(linkWithHref, link, 3000, none()))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("Cannot download file: proxy server is not enabled");
  }

  @Test
  void proxyServerShouldBeStarted() {
    SelenideConfig config = new SelenideConfig().proxyEnabled(true).fileDownload(PROXY);
    when(linkWithHref.driver()).thenReturn(new DriverStub(config, mock(Browser.class), mock(WebDriver.class), null));

    assertThatThrownBy(() -> command.download(linkWithHref, link, 3000, none()))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("Cannot download file: proxy server is not started");
  }

  private void emulateServerResponseWithFiles(final File... files) {
    doAnswer(invocation -> {
      filter.downloads().files().addAll(Stream.of(files).map(file -> new DownloadedFile(file, emptyMap())).collect(toList()));
      return null;
    }).when(link).click();
  }
}
