package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.impl.DummyFileNamer;
import com.codeborne.selenide.webdriver.WebDriverFactory;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import java.io.File;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class LazyDriverTest implements WithAssertions {
  private final Config config = mock(Config.class);
  private final WebDriver webdriver = mock(WebDriver.class);
  private final WebDriverFactory factory = mock(WebDriverFactory.class);
  private final BrowserHealthChecker browserHealthChecker = mock(BrowserHealthChecker.class);
  private final CreateDriverCommand createDriverCommand = new CreateDriverCommand(new DummyFileNamer("123_456_78"));
  private final CloseDriverCommand closeDriverCommand = new CloseDriverCommand();
  private LazyDriver driver;

  @BeforeEach
  void mockLogging() {
    when(config.downloadsFolder()).thenReturn("build/down");
    when(config.reopenBrowserOnFail()).thenReturn(true);
    when(config.proxyEnabled()).thenReturn(true);
    driver = new LazyDriver(config, null, emptyList(), factory, browserHealthChecker, createDriverCommand, closeDriverCommand);
  }

  @BeforeEach
  void setUp() {
    doReturn(webdriver).when(factory).createWebDriver(any(), any(), any());
    doReturn(webdriver).when(factory).createWebDriver(any(), isNull(), any());
  }

  @Test
  void createWebDriverWithoutProxy() {
    when(config.proxyEnabled()).thenReturn(false);

    driver.createDriver();

    verify(factory).createWebDriver(config, null, new File("build/down/123_456_78"));
  }

  @Test
  void createWebDriverWithSelenideProxyServer() {
    when(config.proxyEnabled()).thenReturn(true);

    driver.createDriver();

    assertThat(driver.getProxy()).isNotNull();
    verify(factory).createWebDriver(config, driver.getProxy().createSeleniumProxy(), new File("build/down/123_456_78"));
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
  void closeWebDriver() {
    when(config.holdBrowserOpen()).thenReturn(false);
    when(config.proxyEnabled()).thenReturn(true);

    driver = new LazyDriver(config, mockProxy("selenide:0"), emptyList(),
      factory, browserHealthChecker, createDriverCommand, closeDriverCommand);
    givenOpenedBrowser();

    driver.close();

    assertThat(driver.hasWebDriverStarted()).isFalse();
  }

  private Proxy mockProxy(String httpProxy) {
    Proxy mockedProxy = mock(Proxy.class);
    when(mockedProxy.getHttpProxy()).thenReturn(httpProxy);
    return mockedProxy;
  }

  private void givenOpenedBrowser() {
    assertThat(driver.getAndCheckWebDriver()).isSameAs(webdriver);
  }
}
