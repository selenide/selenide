package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.UnitTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.mockito.Mockito.mock;

class TransferBrowserCapabilitiesFromConfigurationTest extends UnitTest {
  private static final String SOME_CAP = "some.cap";
  private AbstractDriverFactory driverFactory;
  private Proxy proxy = mock(Proxy.class);

  @AfterEach
  void clearConfiguration() {
    Configuration.browserCapabilities = null;
    System.clearProperty(SOME_CAP);
  }

  @BeforeEach
  void createFactory() {
    driverFactory = new ChromeDriverFactory();
    DesiredCapabilities configurationCapabilities = new DesiredCapabilities();
    configurationCapabilities.setCapability(SOME_CAP, "SOME_VALUE_FROM_CONFIGURATION");
    Configuration.browserCapabilities = configurationCapabilities;
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
