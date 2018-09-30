package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Configuration;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.mockito.Mockito.mock;

class TransferBrowserCapabilitiesFromConfigurationTest implements WithAssertions {
  private static final String SOME_CAP = "some.cap";
  private AbstractDriverFactory driverFactory;
  private Proxy proxy = mock(Proxy.class);

  @AfterEach
  void clearConfiguration() {
    Configuration.browserCapabilities = new DesiredCapabilities();
    System.clearProperty(SOME_CAP);
  }

  @BeforeEach
  void createFactory() {
    driverFactory = new ChromeDriverFactory();
    Configuration.browserCapabilities.setCapability(SOME_CAP, "SOME_VALUE_FROM_CONFIGURATION");
  }

  @Test
  void transferCapabilitiesFromConfiguration() {
    DesiredCapabilities someCapabilities = new DesiredCapabilities();
    someCapabilities.setCapability(SOME_CAP, "SOME_VALUE");
    DesiredCapabilities mergedCapabilities = driverFactory.mergeCapabilitiesFromConfiguration(someCapabilities);

    assertThat(mergedCapabilities.getCapability(SOME_CAP))
      .isEqualTo("SOME_VALUE_FROM_CONFIGURATION");
  }

  @Test
  void overrideCapabilitiesFromConfiguration() {
    System.setProperty(SOME_CAP, "SOME_VALUE_FROM_ENV_VARIABLE");
    assertThat(driverFactory.createCommonCapabilities(proxy).getCapability(SOME_CAP))
      .isEqualTo("SOME_VALUE_FROM_CONFIGURATION");
  }
}
