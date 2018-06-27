package com.codeborne.selenide.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.UnitTest;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.webdriver.WebDriverFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import static com.codeborne.selenide.Configuration.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.Configuration.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.close;
import static java.lang.Thread.currentThread;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebDriverThreadLocalContainerTest extends UnitTest {
  private static final Logger log = Logger.getLogger(WebDriverThreadLocalContainer.class.getName());
  private static OutputStream logCapturingStream;
  private static StreamHandler customLogHandler;
  private final WebDriverThreadLocalContainer container = spy(new WebDriverThreadLocalContainer());

  @BeforeEach
  void setUp() {
    container.factory = mock(WebDriverFactory.class);
    doReturn(mock(WebDriver.class)).when(container.factory).createWebDriver(any());
    doReturn(mock(WebDriver.class)).when(container.factory).createWebDriver(null);
    WebDriverRunner.setProxy(null);
    logCapturingStream = new ByteArrayOutputStream();
    Handler[] handlers = log.getParent().getHandlers();
    customLogHandler = new StreamHandler(logCapturingStream, handlers[0].getFormatter());
    log.addHandler(customLogHandler);
  }

  @BeforeEach
  @AfterEach
  void resetSetting() {
    Configuration.reopenBrowserOnFail = true;
  }

  @AfterEach
  void tearDown() {
    WebDriverRunner.setProxy(null);
    close();
  }

  @Test
  void createWebDriverWithoutProxy() {
    Configuration.fileDownload = HTTPGET;

    container.createDriver();

    verify(container.factory).createWebDriver(null);
  }

  @Test
  void createWebDriverWithSelenideProxyServer() {
    Configuration.fileDownload = PROXY;

    container.createDriver();

    ArgumentCaptor<Proxy> captor = ArgumentCaptor.forClass(Proxy.class);
    verify(container.factory).createWebDriver(captor.capture());
    assertThat(captor.getValue().getHttpProxy())
      .isNotNull();
    assertThat(captor.getValue().getSslProxy())
      .isNotNull();
  }

  @Test
  void checksIfBrowserIsStillAlive() {
    Configuration.reopenBrowserOnFail = true;
    WebDriver webdriver = mock(WebDriver.class);
    container.THREAD_WEB_DRIVER.put(currentThread().getId(), webdriver);

    assertThat(container.getAndCheckWebDriver())
      .isEqualTo(webdriver);
    verify(container).isBrowserStillOpen(any());
  }

  @Test
  void doesNotReopenBrowserIfItFailed() {
    Configuration.reopenBrowserOnFail = false;
    WebDriver webdriver = mock(WebDriver.class);
    container.THREAD_WEB_DRIVER.put(currentThread().getId(), webdriver);

    assertThat(container.getAndCheckWebDriver())
      .isEqualTo(webdriver);
    verify(container, never()).isBrowserStillOpen(any());
  }

  @Test
  void checksIfBrowserIsStillAlive_byCallingGetTitle() {
    WebDriver webdriver = mock(WebDriver.class);
    doReturn("blah").when(webdriver).getTitle();

    assertThat(container.isBrowserStillOpen(webdriver))
      .isTrue();
  }

  @Test
  void isBrowserStillOpen_UnreachableBrowserException() {
    WebDriver webdriver = mock(WebDriver.class);
    doThrow(UnreachableBrowserException.class).when(webdriver).getTitle();

    assertThat(container.isBrowserStillOpen(webdriver))
      .isFalse();
  }

  @Test
  void isBrowserStillOpen_NoSuchWindowException() {
    WebDriver webdriver = mock(WebDriver.class);
    doThrow(NoSuchWindowException.class).when(webdriver).getTitle();

    assertThat(container.isBrowserStillOpen(webdriver))
      .isFalse();
  }

  @Test
  void isBrowserStillOpen_NoSuchSessionException() {
    WebDriver webdriver = mock(WebDriver.class);
    doThrow(NoSuchSessionException.class).when(webdriver).getTitle();

    assertThat(container.isBrowserStillOpen(webdriver))
      .isFalse();
  }

  @Test
  void closeWebDriverLoggingWhenProxyIsAdded() {
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
    assertThat(capturedLog)
      .contains(String.format("Close webdriver: %s -> %s", currentThreadId, mockedWebDriver.toString()));
    assertThat(capturedLog)
      .contains(String.format("Close proxy server: %s ->", currentThreadId));
  }

  private static String getTestCapturedLog() {
    customLogHandler.flush();
    return logCapturingStream.toString();
  }

  @Test
  void shouldNotOpenANewBrowser_ifSettingIsDisabled() {
    Configuration.reopenBrowserOnFail = false;

    try {
      container.getWebDriver();
      fail("expected IllegalStateException");
    } catch (IllegalStateException expected) {
      assertThat(expected)
        .hasMessageContaining("reopenBrowserOnFail=false");
    }
  }
}
