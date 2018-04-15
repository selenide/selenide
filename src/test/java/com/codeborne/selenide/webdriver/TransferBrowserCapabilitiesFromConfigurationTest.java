package com.codeborne.selenide.webdriver;


import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import com.codeborne.selenide.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.DesiredCapabilities;

public class TransferBrowserCapabilitiesFromConfigurationTest {

  private AbstractDriverFactory driverFactory;
  private Proxy proxy = mock(Proxy.class);
  private static final String SOME_CAP = "some.cap";

  @After
  public void clearConfiguration() {
    Configuration.browserCapabilities = null;
    System.clearProperty(SOME_CAP);
  }

  @Before
  public void createFactory() {
    driverFactory = new ChromeDriverFactory();
    DesiredCapabilities configurationCapabilities = new DesiredCapabilities();
    configurationCapabilities.setCapability(SOME_CAP, "SOME_VALUE_FROM_CONFIGURATION");
    Configuration.browserCapabilities = configurationCapabilities;
  }

  @Test
  public void transferCapabilitiesFromConfiguration() {
    DesiredCapabilities someCapabilities = new DesiredCapabilities();
    someCapabilities.setCapability(SOME_CAP, "SOME_VALUE");
    DesiredCapabilities mergedCapabilities = driverFactory.mergeCapabilitiesFromConfiguration(someCapabilities);

    assertThat(mergedCapabilities.getCapability(SOME_CAP), is("SOME_VALUE_FROM_CONFIGURATION"));
  }

  @Test
  public void overrideCapabilitiesFromConfiguration() {
    System.setProperty(SOME_CAP, "SOME_VALUE_FROM_ENV_VARIABLE");
    assertThat(driverFactory.createCommonCapabilities(proxy).getCapability(SOME_CAP),
        is("SOME_VALUE_FROM_CONFIGURATION"));
  }

}
