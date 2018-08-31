package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.close;
import static org.mockito.Mockito.spy;

class WebDriverThreadLocalContainerTest implements WithAssertions {
  private final WebDriverThreadLocalContainer container = spy(new WebDriverThreadLocalContainer());

  @BeforeEach
  void setUp() {
    WebDriverRunner.setProxy(null);
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
}
