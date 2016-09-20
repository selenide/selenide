package com.codeborne.selenide.impl;

import com.codeborne.selenide.proxy.FileDownloadFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.codeborne.selenide.rules.MockWebdriverContainer;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.codeborne.selenide.WebDriverRunner.webdriverContainer;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DownloadFileWithProxyServerTest {
  @Rule
  public MockWebdriverContainer mockWebdriverContainer = new MockWebdriverContainer();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  DownloadFileWithProxyServer command = new DownloadFileWithProxyServer();
  WebDriver webdriver = mock(WebDriver.class);
  SelenideProxyServer proxy = mock(SelenideProxyServer.class);
  WebElementSource linkWithHref = mock(WebElementSource.class);
  WebElement link = mock(WebElement.class);

  FileDownloadFilter filter = spy(new FileDownloadFilter());

  @Before
  public void setUp() {
    command.waiter = spy(new Waiter());
    doNothing().when(command.waiter).sleep(anyLong());
    when(webdriverContainer.getWebDriver()).thenReturn(webdriver);
    when(webdriver.switchTo()).thenReturn(mock(TargetLocator.class));
    
    when(proxy.responseFilter("download")).thenReturn(filter);
    when(linkWithHref.findAndAssertElementIsVisible()).thenReturn(link);
    when(linkWithHref.toString()).thenReturn("<a href='report.pdf'>report</a>");
  }

  @Test
  public void canInterceptFileViaProxyServer() throws IOException {
    emulateServerResponseWithFiles(new File("report.pdf"));

    File file = command.download(linkWithHref, link, proxy);
    assertThat(file.getName(), is("report.pdf"));

    verify(filter).activate();
    verify(link).click();
    verify(filter).deactivate();
  }
  
  @Test
  public void closesNewWindowIfFileWasOpenedInSeparateWindow() throws IOException {
    emulateServerResponseWithFiles(new File("report.pdf"));
    when(webdriver.getWindowHandle()).thenReturn("tab1");
    when(webdriver.getWindowHandles())
        .thenReturn(ImmutableSet.of("tab1", "tab2", "tab3"))
        .thenReturn(ImmutableSet.of("tab1", "tab2", "tab3", "tab-with-pdf"));

    File file = command.download(linkWithHref, link, proxy);
    assertThat(file.getName(), is("report.pdf"));

    verify(webdriver.switchTo()).window("tab-with-pdf");
    verify(webdriver).close();
    verify(webdriver.switchTo()).window("tab1");
    verifyNoMoreInteractions(webdriver.switchTo());
  }

  @Test
  public void throwsFileNotFoundException_ifNoFilesHaveBeenDownloadedAfterClick() throws IOException {
    emulateServerResponseWithFiles();

    thrown.expect(FileNotFoundException.class);
    thrown.expectMessage("Failed to download file <a href='report.pdf'>report</a>");
    command.download(linkWithHref, link, proxy);
  }

  private void emulateServerResponseWithFiles(final File... files) {
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        filter.getDownloadedFiles().addAll(asList(files));
        return null;
      }
    }).when(link).click();
  }
}
