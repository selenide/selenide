package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.DummyWebDriver;
import com.codeborne.selenide.impl.DummyFileNamer;
import com.codeborne.selenide.webdriver.WebDriverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import java.io.File;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class LazyDriverTest {
  private final Config config = mock();
  private WebDriver webdriver;
  private final WebDriverFactory factory = mock();
  private final BrowserHealthChecker browserHealthChecker = mock();
  private final CreateDriverCommand createDriverCommand = new CreateDriverCommand(new DummyFileNamer("123_456_78"));
  private LazyDriver driver;

  @BeforeEach
  void mockLogging() {
    when(config.downloadsFolder()).thenReturn("build/down");
    when(config.proxyEnabled()).thenReturn(true);
    driver = new LazyDriver(config, null, emptyList(), emptyList(),
      factory, browserHealthChecker, createDriverCommand);
  }

  @BeforeEach
  void setUp() {
    when(factory.createWebDriver(any(), any(), any())).thenAnswer((Answer<WebDriver>) invocation -> {
      webdriver = spy(new DummyWebDriver());
      return webdriver;
    });
  }

  @Test
  void createWebDriverWithoutProxy() {
    when(config.proxyEnabled()).thenReturn(false);

    driver.createDriver();

    verify(factory).createWebDriver(config, null, new File("build/down/123_456_78").getAbsoluteFile());
  }

  @Test
  void createWebDriverWithSelenideProxyServer() {
    when(config.proxyEnabled()).thenReturn(true);

    driver.createDriver();

    assertThat(driver.getProxy()).isNotNull();
    verify(factory).createWebDriver(config, driver.getProxy().getSeleniumProxy(),
      new File("build/down/123_456_78").getAbsoluteFile());
  }

  @Test
  void checksIfBrowserIsStillAlive() {
    givenOpenedBrowser();
    when(config.reopenBrowserOnFail()).thenReturn(true);
    when(browserHealthChecker.isBrowserStillOpen(any())).thenReturn(true);

    assertThat(driver.getAndCheckWebDriver()).isEqualTo(webdriver);
    verify(browserHealthChecker).isBrowserStillOpen(webdriver);
  }

  @Test
  void reopensBrowserIfItsDied() {
    WebDriver diedWebdriver = givenOpenedBrowser();
    when(config.reopenBrowserOnFail()).thenReturn(true);
    when(browserHealthChecker.isBrowserStillOpen(any())).thenReturn(false);

    assertThat(driver.getAndCheckWebDriver()).isNotEqualTo(diedWebdriver);
    verify(browserHealthChecker).isBrowserStillOpen(diedWebdriver);
    verify(diedWebdriver).quit();
  }

  @Test
  void shouldNotReopenBrowserIfItFailed() {
    WebDriver diedWebdriver = givenOpenedBrowser();
    when(config.reopenBrowserOnFail()).thenReturn(false);
    when(browserHealthChecker.isBrowserStillOpen(any())).thenReturn(false);

    assertThatThrownBy(() -> driver.getAndCheckWebDriver())
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("has been closed meanwhile")
      .hasMessageContaining("cannot create a new webdriver because reopenBrowserOnFail=false");
    verify(browserHealthChecker).isBrowserStillOpen(diedWebdriver);
    verify(diedWebdriver).quit();
  }

  @Test
  void getWebDriver_throwsException_ifBrowserIsNotOpen() {
    assertThatThrownBy(() -> driver.getWebDriver())
      .isInstanceOf(IllegalStateException.class)
      .hasMessageStartingWith("No webdriver is bound to current thread")
      .hasMessageEndingWith("You need to call open(url) first.");
  }

  @Test
  void getProxy_throwsException_ifBrowserIsNotOpen() {
    assertThatThrownBy(() -> driver.getProxy())
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

    driver = new LazyDriver(config, mockProxy("selenide:0"), emptyList(), emptyList(),
      factory, browserHealthChecker, createDriverCommand);
    givenOpenedBrowser();

    driver.close();

    assertThat(driver.hasWebDriverStarted()).isFalse();
  }

  private Proxy mockProxy(String httpProxy) {
    Proxy mockedProxy = mock();
    when(mockedProxy.getHttpProxy()).thenReturn(httpProxy);
    return mockedProxy;
  }

  private WebDriver givenOpenedBrowser() {
    assertThat(driver.getAndCheckWebDriver()).isSameAs(webdriver);
    return webdriver;
  }
}
