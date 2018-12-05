package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.proxy.FileDownloadFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.google.common.collect.ImmutableSet;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class DownloadFileWithProxyServerTest implements WithAssertions {
  private Waiter waiter = mock(Waiter.class);
  private DownloadFileWithProxyServer command = new DownloadFileWithProxyServer(waiter);
  private final SelenideConfig config = new SelenideConfig();
  private WebDriver webdriver = mock(WebDriver.class);
  private SelenideProxyServer proxy = mock(SelenideProxyServer.class);
  private WebElementSource linkWithHref = mock(WebElementSource.class);
  private WebElement link = mock(WebElement.class);
  private FileDownloadFilter filter = spy(new FileDownloadFilter(new SelenideConfig().reportsFolder("build/downloads")));

  @BeforeEach
  void setUp() {
    doNothing().when(waiter).wait(any(), any(), anyLong(), anyLong());
    when(webdriver.switchTo()).thenReturn(mock(TargetLocator.class));

    when(proxy.responseFilter("download")).thenReturn(filter);
    when(linkWithHref.driver()).thenReturn(new DriverStub(config, new Browser("opera", false), webdriver, proxy));
    when(linkWithHref.findAndAssertElementIsInteractable()).thenReturn(link);
    when(linkWithHref.toString()).thenReturn("<a href='report.pdf'>report</a>");
  }

  @Test
  void canInterceptFileViaProxyServer() throws IOException {
    emulateServerResponseWithFiles(new File("report.pdf"));

    File file = command.download(linkWithHref, link, proxy, 3000);
    assertThat(file.getName())
      .isEqualTo("report.pdf");

    verify(filter).activate();
    verify(link).click();
    verify(filter).deactivate();
  }

  private void emulateServerResponseWithFiles(final File... files) {
    doAnswer(invocation -> {
      filter.getDownloadedFiles().addAll(asList(files));
      return null;
    }).when(link).click();
  }

  @Test
  void closesNewWindowIfFileWasOpenedInSeparateWindow() throws IOException {
    emulateServerResponseWithFiles(new File("report.pdf"));
    when(webdriver.getWindowHandle()).thenReturn("tab1");
    when(webdriver.getWindowHandles())
      .thenReturn(ImmutableSet.of("tab1", "tab2", "tab3"))
      .thenReturn(ImmutableSet.of("tab1", "tab2", "tab3", "tab-with-pdf"));

    File file = command.download(linkWithHref, link, proxy, 3000);
    assertThat(file.getName())
      .isEqualTo("report.pdf");

    verify(webdriver.switchTo()).window("tab-with-pdf");
    verify(webdriver).close();
    verify(webdriver.switchTo()).window("tab1");
    verifyNoMoreInteractions(webdriver.switchTo());
  }

  @Test
  void ignoresErrorIfWindowHasAlreadyBeenClosedMeanwhile() throws IOException {
    TargetLocator targetLocator = mock(TargetLocator.class);
    doReturn(targetLocator).when(webdriver).switchTo();
    doThrow(new NoSuchWindowException("no window: tab-with-pdf")).when(targetLocator).window("tab-with-pdf");

    emulateServerResponseWithFiles(new File("report.pdf"));
    when(webdriver.getWindowHandle()).thenReturn("tab1");
    when(webdriver.getWindowHandles())
      .thenReturn(ImmutableSet.of("tab1", "tab2", "tab3"))
      .thenReturn(ImmutableSet.of("tab1", "tab2", "tab3", "tab-with-pdf"));

    File file = command.download(linkWithHref, link, proxy, 3000);
    assertThat(file.getName())
      .isEqualTo("report.pdf");

    verify(webdriver.switchTo()).window("tab-with-pdf");
    verify(webdriver, never()).close();
    verify(webdriver.switchTo()).window("tab1");
    verifyNoMoreInteractions(webdriver.switchTo());
  }

  @Test
  void throwsFileNotFoundExceptionIfNoFilesHaveBeenDownloadedAfterClick() {
    emulateServerResponseWithFiles();

    assertThatThrownBy(() -> command.download(linkWithHref, link, proxy, 3000))
      .isInstanceOf(FileNotFoundException.class)
      .hasMessageStartingWith("Failed to download file <a href='report.pdf'>report</a>");
  }
}
