package com.codeborne.selenide;

import com.codeborne.selenide.drivercommands.BrowserHealthChecker;
import com.codeborne.selenide.drivercommands.CloseDriverCommand;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SelenideDriverTest implements WithAssertions {
  private static final Logger log = Logger.getLogger(CloseDriverCommand.class.getName());

  private static OutputStream logCapturingStream;
  private static StreamHandler customLogHandler;

  WebDriver webdriver = mock(WebDriver.class);
  WebDriverFactory factory = mock(WebDriverFactory.class);
  BrowserHealthChecker browserHealthChecker = mock(BrowserHealthChecker.class);
  SelenideDriver driver = new SelenideDriver(null, emptyList(), factory, browserHealthChecker);

  @BeforeEach
  void mockLogging() {
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
    doReturn(webdriver).when(factory).createWebDriver(any());
    doReturn(webdriver).when(factory).createWebDriver(null);
  }

  @BeforeEach
  @AfterEach
  void resetSettings() {
    Configuration.reopenBrowserOnFail = true;
  }

  @Test
  void createWebDriverWithoutProxy() {
    Configuration.proxyEnabled = false;

    driver.createDriver();

    verify(factory).createWebDriver(null);
  }

  @Test
  void createWebDriverWithSelenideProxyServer() {
    Configuration.proxyEnabled = true;

    driver.createDriver();

    assertThat(driver.getProxy()).isNotNull();
    verify(factory).createWebDriver(driver.getProxy().createSeleniumProxy());
  }

  @Test
  void checksIfBrowserIsStillAlive() {
    Configuration.reopenBrowserOnFail = true;
    driver = new SelenideDriver(webdriver, factory, browserHealthChecker);

    assertThat(driver.getAndCheckWebDriver()).isEqualTo(webdriver);
    verify(browserHealthChecker).isBrowserStillOpen(any());
  }

  @Test
  void doesNotReopenBrowserIfItFailed() {
    driver.getWebDriver();
    Configuration.reopenBrowserOnFail = false;

    assertThat(driver.getAndCheckWebDriver()).isEqualTo(webdriver);
    verify(browserHealthChecker, never()).isBrowserStillOpen(any());
  }

  @Test
  void closeWebDriverLoggingWhenProxyIsAdded() {
    Configuration.holdBrowserOpen = false;
    Configuration.proxyEnabled = true;

    Proxy mockedProxy = mock(Proxy.class);
    when(mockedProxy.getHttpProxy()).thenReturn("selenide:0");
    driver = new SelenideDriver(mockedProxy, emptyList(), factory, browserHealthChecker);
    driver.getWebDriver();

    driver.close();

    String capturedLog = getTestCapturedLog();
    String currentThreadId = String.valueOf(currentThread().getId());
    assertThat(capturedLog)
      .contains(String.format("Close webdriver: %s -> %s", currentThreadId, webdriver.toString()));
    assertThat(capturedLog)
      .contains(String.format("Close proxy server: %s ->", currentThreadId));
  }

  private static String getTestCapturedLog() {
    customLogHandler.flush();
    return logCapturingStream.toString();
  }
}
