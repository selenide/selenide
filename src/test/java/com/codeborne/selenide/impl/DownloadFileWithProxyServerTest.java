package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.DummyWebDriver;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.files.DownloadedFile;
import com.codeborne.selenide.proxy.FileDownloadFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.util.stream.Stream;

import static com.codeborne.selenide.DownloadOptions.ContentStrategy.KEEP_CONTENT;
import static com.codeborne.selenide.DownloadOptions.file;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class DownloadFileWithProxyServerTest {
  private final Waiter waiter = new DummyWaiter();
  private final DownloadFileWithProxyServer command = new DownloadFileWithProxyServer(waiter);
  private final SelenideConfig config = new SelenideConfig();
  private final WebDriver webdriver = new DummyWebDriver();
  private final SelenideProxyServer proxy = mock();
  private final WebElementSource linkWithHref = mock();
  private final WebElement link = mock();
  private final FileDownloadFilter filter = spy(new FileDownloadFilter(config));

  @BeforeEach
  void setUp() {
    config.proxyEnabled(true);
    config.fileDownload(PROXY);
    when(proxy.responseFilter("selenide.proxy.filter.download")).thenReturn(filter);
    when(linkWithHref.driver()).thenReturn(new DriverStub(config, new Browser("opera", false), webdriver, proxy));
    when(linkWithHref.findAndAssertElementIsInteractable()).thenReturn(link);
    when(linkWithHref.toString()).thenReturn("<a href='report.pdf'>report</a>");
  }

  @Test
  void canInterceptFileViaProxyServer() {
    emulateServerResponseWithFiles(new File("report.pdf"));

    File file = command.download(linkWithHref, link, 3000, file());

    assertThat(file.getName()).isEqualTo("report.pdf");
    verify(filter).activate(KEEP_CONTENT);
    verify(link).click();
    verify(filter).deactivate();
  }

  @Test
  void closesNewWindowIfFileWasOpenedInSeparateWindow() {
    emulateServerResponseWithFiles(new File("report.pdf"));

    File file = command.download(linkWithHref, link, 3000, file());

    assertThat(file.getName()).isEqualTo("report.pdf");
  }

  @Test
  void throws_FileNotDownloaded_ifNoFilesHaveBeenDownloadedAfterClick() {
    emulateServerResponseWithFiles();

    assertThatThrownBy(() -> command.download(linkWithHref, link, 3000, file()))
      .isInstanceOf(FileNotDownloadedError.class)
      .hasMessageStartingWith("Failed to download file in 3s");
  }

  @Test
  void proxyServerShouldBeEnabled() {
    config.proxyEnabled(false);
    config.fileDownload(PROXY);

    assertThatThrownBy(() -> command.download(linkWithHref, link, 3000, file()))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("Cannot download file: proxy server is not enabled");
  }

  @Test
  void proxyServerShouldBeStarted() {
    when(linkWithHref.driver()).thenReturn(new DriverStub(config, mock(), new DummyWebDriver(), null));

    assertThatThrownBy(() -> command.download(linkWithHref, link, 3000, file()))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("config.proxyEnabled == true but proxy server is not created.");
  }

  private void emulateServerResponseWithFiles(final File... files) {
    doAnswer(invocation -> {
      filter.downloads().files().addAll(Stream.of(files).map(file -> new DownloadedFile(file, 0, 0, emptyMap())).toList());
      return null;
    }).when(link).click();
  }
}
