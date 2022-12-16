package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.drivercommands.BrowserHealthChecker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class WebDriverThreadLocalContainerTest {
  private final BrowserHealthChecker browserHealthChecker = mock();
  private final WebDriverThreadLocalContainer container = new WebDriverThreadLocalContainer(browserHealthChecker);

  @BeforeEach
  void mockWebDriver() {
    WebDriverRunner.setProxy(null);
    Configuration.holdBrowserOpen = false;
    Configuration.reopenBrowserOnFail = true;
    Configuration.browserSize = null;
    Configuration.browser = DummyProvider.class.getName();
  }

  @AfterEach
  void resetSetting() {
    Configuration.holdBrowserOpen = false;
    Configuration.reopenBrowserOnFail = true;
    Configuration.browser = "firefox";
  }

  @AfterEach
  void tearDown() {
    WebDriverRunner.setProxy(null);
    closeWebDriver();
  }

  @Test
  void hasWebDriverStarted_false_ifNoDriverBoundToCurrentThread() {
    assertThat(container.hasWebDriverStarted()).isFalse();
  }

  @Test
  void hasWebDriverStarted_true_if_browserIsOpened() {
    assertThat(container.getAndCheckWebDriver()).isNotNull();
    assertThat(container.hasWebDriverStarted()).isTrue();
  }

  @Test
  void close_doesNothing_ifBrowserIsNotOpened() {
    container.closeWebDriver();
    assertThat(container.hasWebDriverStarted()).isFalse();
  }

  @Test
  void close_unbinds_webdriver_from_current_thread() {
    assertThat(container.getAndCheckWebDriver()).isNotNull();
    assertThat(container.hasWebDriverStarted()).isTrue();

    container.closeWebDriver();

    assertThat(container.hasWebDriverStarted()).isFalse();
  }

  @Test
  void holdsAllBrowsers_toAutomaticallyCloseThem() {
    WebDriver webDriver = container.getAndCheckWebDriver();

    assertThat(webDriver).isNotNull();
    assertThat(container.allWebDriverThreads).hasSize(1);
    assertThat(container.threadWebDriver).hasSize(1);
    assertThat(container.threadWebDriver.get(container.allWebDriverThreads.iterator().next().getId()).webDriver()).isSameAs(webDriver);
    assertThat(container.isDeadThreadsWatchdogStarted()).isTrue();
  }

  @Test
  void doesNotCloseBrowsers_ifHoldBrowserOpenSettingIsTrue() {
    Configuration.holdBrowserOpen = true;

    WebDriver webDriver = container.getAndCheckWebDriver();

    assertThat(webDriver).isNotNull();
    assertThat(container.allWebDriverThreads).hasSize(0);
    assertThat(container.isDeadThreadsWatchdogStarted()).isFalse();
  }

  @Test
  void checksIfBrowser_hasDiedMeanwhile() {
    WebDriver oldDriver = container.getAndCheckWebDriver();
    when(browserHealthChecker.isBrowserStillOpen(oldDriver)).thenReturn(true);

    WebDriver webDriver = container.getAndCheckWebDriver();

    assertThat(webDriver).isSameAs(oldDriver);
    verify(browserHealthChecker).isBrowserStillOpen(oldDriver);
  }

  @Test
  void reopensBrowser_ifItHasDiedMeanwhile() {
    WebDriver oldDriver = container.getAndCheckWebDriver();
    when(browserHealthChecker.isBrowserStillOpen(oldDriver)).thenReturn(false);

    WebDriver webDriver = container.getAndCheckWebDriver();
    assertThat(webDriver).isNotNull();
    assertThat(webDriver).isNotEqualTo(oldDriver);
    verify(oldDriver).quit();
    assertThat(container.allWebDriverThreads).hasSize(2);
    assertThat(container.threadWebDriver).hasSize(1);
    assertThat(container.threadWebDriver.get(container.allWebDriverThreads.iterator().next().getId()).webDriver()).isSameAs(webDriver);
    assertThat(container.isDeadThreadsWatchdogStarted()).isTrue();
  }

  @Test
  void shouldNotReopenBrowser_if_reopenBrowserOnFail_isFalse() {
    Configuration.reopenBrowserOnFail = false;
    WebDriver oldDriver = container.getAndCheckWebDriver();
    when(browserHealthChecker.isBrowserStillOpen(oldDriver)).thenReturn(false);

    assertThatThrownBy(() -> container.getAndCheckWebDriver())
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("has been closed meanwhile")
      .hasMessageContaining("cannot create a new webdriver because reopenBrowserOnFail=false");
    verify(oldDriver).quit();
    assertThat(container.allWebDriverThreads).hasSize(1);
    assertThat(container.threadWebDriver).hasSize(0);
    assertThat(container.isDeadThreadsWatchdogStarted()).isTrue();
  }

  private static class DummyProvider implements WebDriverProvider {
    @Override
    @CheckReturnValue
    @Nonnull
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
      WebDriver webdriver = mock();
      WebDriver.Options options = mock();
      when(webdriver.manage()).thenReturn(options);
      when(options.timeouts()).thenReturn(mock());
      return webdriver;
    }
  }
}
