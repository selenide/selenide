package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.webdriver.WebDriverFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import static com.codeborne.selenide.Configuration.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.Configuration.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.close;
import static java.lang.Thread.currentThread;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

public class WebDriverThreadLocalContainerTest {
  private final WebDriverThreadLocalContainer container = spy(new WebDriverThreadLocalContainer());
  private static final Logger log =
      Logger.getLogger(WebDriverThreadLocalContainer.class.getName()); // matches the logger in the affected class
  private static OutputStream logCapturingStream;
  private static StreamHandler customLogHandler;

  private static String getTestCapturedLog() {
    customLogHandler.flush();
    return logCapturingStream.toString();
  }

  @Before
  public void setUp() {
    container.factory = mock(WebDriverFactory.class);
    doReturn(mock(WebDriver.class)).when(container.factory).createWebDriver(any());
    doReturn(mock(WebDriver.class)).when(container.factory).createWebDriver(null);
    WebDriverRunner.setProxy(null);
    logCapturingStream = new ByteArrayOutputStream();
    Handler[] handlers = log.getParent().getHandlers();
    customLogHandler = new StreamHandler(logCapturingStream, handlers[0].getFormatter());
    log.addHandler(customLogHandler);

  }

  @After
  public void tearDown() {
    WebDriverRunner.setProxy(null);
    close();
  }

  @Test
  public void createWebDriverWithoutProxy() {
    Configuration.fileDownload = HTTPGET;

    container.createDriver();

    verify(container.factory).createWebDriver(null);
  }

  @Test
  public void createWebDriverWithSelenideProxyServer() {
    Configuration.fileDownload = PROXY;

    container.createDriver();

    ArgumentCaptor<Proxy> captor = ArgumentCaptor.forClass(Proxy.class);
    verify(container.factory).createWebDriver(captor.capture());
    assertThat(captor.getValue().getHttpProxy(), is(notNullValue()));
    assertThat(captor.getValue().getSslProxy(), is(notNullValue()));
  }

  @Test
  public void checksIfBrowserIsStillAlive() {
    Configuration.reopenBrowserOnFail = true;
    WebDriver webdriver = mock(WebDriver.class);
    container.THREAD_WEB_DRIVER.put(currentThread().getId(), webdriver);

    assertSame(webdriver, container.getAndCheckWebDriver());
    verify(container).isBrowserStillOpen(any());
  }

  @Test
  public void doesNotReopenBrowserIfItFailed() {
    Configuration.reopenBrowserOnFail = false;
    WebDriver webdriver = mock(WebDriver.class);
    container.THREAD_WEB_DRIVER.put(currentThread().getId(), webdriver);

    assertSame(webdriver, container.getAndCheckWebDriver());
    verify(container, never()).isBrowserStillOpen(any());
  }

  @Test
  public void checksIfBrowserIsStillAlive_byCallingGetTitle() {
    WebDriver webdriver = mock(WebDriver.class);
    doReturn("blah").when(webdriver).getTitle();

    assertThat(container.isBrowserStillOpen(webdriver), is(true));
  }

  @Test
  public void isBrowserStillOpen_UnreachableBrowserException() {
    WebDriver webdriver = mock(WebDriver.class);
    doThrow(UnreachableBrowserException.class).when(webdriver).getTitle();

    assertThat(container.isBrowserStillOpen(webdriver), is(false));
  }

  @Test
  public void isBrowserStillOpen_NoSuchWindowException() {
    WebDriver webdriver = mock(WebDriver.class);
    doThrow(NoSuchWindowException.class).when(webdriver).getTitle();

    assertThat(container.isBrowserStillOpen(webdriver), is(false));
  }

  @Test
  public void isBrowserStillOpen_NoSuchSessionException() {
    WebDriver webdriver = mock(WebDriver.class);
    doThrow(NoSuchSessionException.class).when(webdriver).getTitle();

    assertThat(container.isBrowserStillOpen(webdriver), is(false));
  }

  @Test
  public void closeWebDriverLoggingWhenProxyIsAdded() throws IOException {
    Configuration.holdBrowserOpen = false;
    Configuration.fileDownload = PROXY;

    Proxy mockedProxy = Mockito.mock(Proxy.class);
    when(mockedProxy.getHttpProxy()).thenReturn("selenide:0");
    container.setProxy(mockedProxy);
    container.createDriver();

    ChromeDriver mockedWebDriver = Mockito.mock(ChromeDriver.class);
    container.setWebDriver(mockedWebDriver);

    container.closeWebDriver();

    String capturedLog = getTestCapturedLog();
    String currentThreadId = String.valueOf(currentThread().getId());
    assertThat(capturedLog, containsString(String.format("Close webdriver: %s -> %s", currentThreadId, mockedWebDriver.toString())));
    assertThat(capturedLog, containsString(String.format("Close proxy server: %s ->", currentThreadId)));
  }

}
