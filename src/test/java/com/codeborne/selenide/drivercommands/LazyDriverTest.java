package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.webdriver.WebDriverFactory;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import static java.lang.Thread.currentThread;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LazyDriverTest implements WithAssertions {
  private static final Logger log = Logger.getLogger(CloseDriverCommand.class.getName());

  private static OutputStream logCapturingStream;
  private static StreamHandler customLogHandler;

  private Config config = mock(Config.class);
  private WebDriver webdriver = mock(WebDriver.class);
  private WebDriverFactory factory = mock(WebDriverFactory.class);
  private BrowserHealthChecker browserHealthChecker = mock(BrowserHealthChecker.class);
  private LazyDriver driver;

  @BeforeEach
  void mockLogging() {
    when(config.reopenBrowserOnFail()).thenReturn(true);
    when(config.proxyEnabled()).thenReturn(true);
    driver = new LazyDriver(config, null, emptyList(), factory, browserHealthChecker);

    logCapturingStream = new ByteArrayOutputStream();
    Handler[] handlers = log.getParent().getHandlers();
    customLogHandler = new StreamHandler(logCapturingStream, handlers[0].getFormatter());
    log.addHandler(customLogHandler);
  }

  @AfterEach
  void undoLoggingMock() {
    log.removeHandler(customLogHandler);
  }

  @BeforeEach
  void setUp() {
    doReturn(webdriver).when(factory).createWebDriver(any(), any());
    doReturn(webdriver).when(factory).createWebDriver(any(), isNull());
  }

  @Test
  void createWebDriverWithoutProxy() {
    when(config.proxyEnabled()).thenReturn(false);

    driver.createDriver();

    verify(factory).createWebDriver(config, null);
  }

  @Test
  void createWebDriverWithSelenideProxyServer() {
    when(config.proxyEnabled()).thenReturn(true);

    driver.createDriver();

    assertThat(driver.getProxy()).isNotNull();
    verify(factory).createWebDriver(config, driver.getProxy().createSeleniumProxy());
  }

  @Test
  void checksIfBrowserIsStillAlive() {
    givenOpenedBrowser();
    when(config.reopenBrowserOnFail()).thenReturn(true);

    assertThat(driver.getAndCheckWebDriver()).isEqualTo(webdriver);
    verify(browserHealthChecker).isBrowserStillOpen(any());
  }

  @Test
  void doesNotReopenBrowserIfItFailed() {
    givenOpenedBrowser();
    when(config.reopenBrowserOnFail()).thenReturn(false);

    assertThat(driver.getAndCheckWebDriver()).isEqualTo(webdriver);
    verify(browserHealthChecker, never()).isBrowserStillOpen(any());
  }

  @Test
  void getWebDriver_throwsException_ifBrowserIsNotOpen() {
    assertThatThrownBy(() -> driver.getWebDriver())
      .isInstanceOf(IllegalStateException.class)
      .hasMessageStartingWith("No webdriver is bound to current thread")
      .hasMessageEndingWith("You need to call open(url) first.");
  }

  @Test
  void getWebDriver_throwsException_ifBrowserHasBeenClosed() {
    driver.getAndCheckWebDriver();
    driver.close();

    assertThatThrownBy(() -> driver.getWebDriver())
      .isInstanceOf(IllegalStateException.class)
      .hasMessageStartingWith("Webdriver has been closed")
      .hasMessageEndingWith("You need to call open(url) to open a browser again.");
  }

  @Test
  void closeWebDriverLoggingWhenProxyIsAdded() {
    when(config.holdBrowserOpen()).thenReturn(false);
    when(config.proxyEnabled()).thenReturn(true);

    driver = new LazyDriver(config, mockProxy("selenide:0"), emptyList(), factory, browserHealthChecker);
    givenOpenedBrowser();

    driver.close();

    String capturedLog = getTestCapturedLog();
    String currentThreadId = String.valueOf(currentThread().getId());
    assertThat(capturedLog)
      .contains(String.format("Close webdriver: %s -> %s", currentThreadId, webdriver.toString()));
    assertThat(capturedLog)
      .contains(String.format("Close proxy server: %s ->", currentThreadId));
  }

  private Proxy mockProxy(String httpProxy) {
    Proxy mockedProxy = mock(Proxy.class);
    when(mockedProxy.getHttpProxy()).thenReturn(httpProxy);
    return mockedProxy;
  }

  private void givenOpenedBrowser() {
    assertThat(driver.getAndCheckWebDriver()).isSameAs(webdriver);
  }

  private static String getTestCapturedLog() {
    customLogHandler.flush();
    return logCapturingStream.toString();
  }
}
