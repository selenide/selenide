package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.mockito.Mockito.mock;

final class TransferBrowserCapabilitiesFromConfigurationTest implements WithAssertions {
  private static final String SOME_CAP = "some.cap";
  private AbstractDriverFactory driverFactory;
  private final Proxy proxy = mock(Proxy.class);
  private final SelenideConfig config = new SelenideConfig();
  private final Browser browser = new Browser(config.browser(), config.headless());

  @AfterEach
  void clearConfiguration() {
    System.clearProperty(SOME_CAP);
  }

  @BeforeEach
  void createFactory() {
    driverFactory = new ChromeDriverFactory();
    config.browserCapabilities().setCapability(SOME_CAP, "SOME_VALUE_FROM_CONFIGURATION");
  }

  @Test
  void transferCapabilitiesFromConfiguration() {
    DesiredCapabilities someCapabilities = new DesiredCapabilities();
    someCapabilities.setCapability(SOME_CAP, "SOME_VALUE");
    DesiredCapabilities mergedCapabilities = someCapabilities.merge(((Config) config).browserCapabilities());

    assertThat(mergedCapabilities.getCapability(SOME_CAP))
      .isEqualTo("SOME_VALUE_FROM_CONFIGURATION");
  }

  @Test
  void overrideCapabilitiesFromConfiguration() {
    System.setProperty(SOME_CAP, "SOME_VALUE_FROM_ENV_VARIABLE");
    assertThat(driverFactory.createCommonCapabilities(config, browser, proxy).getCapability(SOME_CAP))
      .isEqualTo("SOME_VALUE_FROM_CONFIGURATION");
  }
}
