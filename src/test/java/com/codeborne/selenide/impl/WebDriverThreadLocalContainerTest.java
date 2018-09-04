package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.webdriver.WebDriverFactory;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Selenide.close;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class WebDriverThreadLocalContainerTest implements WithAssertions {
  private WebDriverFactory factory = mock(WebDriverFactory.class);
  private final WebDriverThreadLocalContainer container = spy(new WebDriverThreadLocalContainer(factory));

  @BeforeEach
  void setUp() {
    WebDriverRunner.setProxy(null);
    when(factory.createWebDriver(any())).thenReturn(mock(WebDriver.class));
  }

  @BeforeEach
  @AfterEach
  void resetSetting() {
    Configuration.reopenBrowserOnFail = true;
    Configuration.browser = "firefox";
  }

  @AfterEach
  void tearDown() {
    WebDriverRunner.setProxy(null);
    close();
  }

  @Test
  void shouldNotOpenANewBrowser_ifSettingIsDisabled() {
    Configuration.reopenBrowserOnFail = false;

    try {
      container.getWebDriver();
      fail("expected IllegalStateException");
    }
    catch (IllegalStateException expected) {
      assertThat(expected)
        .hasMessageContaining("reopenBrowserOnFail=false");
    }
  }

  @Test
  void hasWebDriverStarted_false_ifNoDriverBoundToCurrentThread() {
    assertThat(container.hasWebDriverStarted()).isFalse();
  }

  @Test
  void hasWebDriverStarted_false_ifDriverIsBoundToCurrentThread_butBrowserIsNotOpened() {
    assertThat(container.getSelenideDriver()).isNotNull();
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
}
