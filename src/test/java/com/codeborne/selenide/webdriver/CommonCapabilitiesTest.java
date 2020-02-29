package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import static com.codeborne.selenide.Browsers.IE;
import static com.codeborne.selenide.Browsers.INTERNET_EXPLORER;
import static org.mockito.Mockito.mock;
import static org.openqa.selenium.remote.CapabilityType.*;

public class CommonCapabilitiesTest implements WithAssertions {
  private AbstractDriverFactory driverFactory;
  private Proxy proxy = mock(Proxy.class);

  @BeforeEach
  void createFactory() {
    driverFactory = new AbstractDriverFactory() {
      @Override
      boolean supports(Config config, Browser browser) {
        return false;
      }

      @Override
      WebDriver create(Config config, Proxy proxy) {
        return null;
      }
    };
  }

  @Test
  void transferCapabilitiesFromConfiguration() {
    SelenideConfig config = new SelenideConfig();
    config.pageLoadStrategy("foo");
    DesiredCapabilities commonCapabilities = driverFactory.createCommonCapabilities(config, proxy);
    assertThat(asBool(commonCapabilities.getCapability(ACCEPT_INSECURE_CERTS))).isTrue();
    assertThat(asBool(commonCapabilities.getCapability(ACCEPT_SSL_CERTS))).isTrue();
    assertThat(commonCapabilities.getCapability(PAGE_LOAD_STRATEGY)).isEqualTo(config.pageLoadStrategy());
  }

  @Test
  void transferCapabilitiesFromConfiguration1() {
    SelenideConfig config = new SelenideConfig();
    config.browser(INTERNET_EXPLORER);
    DesiredCapabilities commonCapabilities = driverFactory.createCommonCapabilities(config, proxy);
    assertThat(asBool(commonCapabilities.getCapability(ACCEPT_INSECURE_CERTS))).isFalse();
    assertThat(asBool(commonCapabilities.getCapability(ACCEPT_SSL_CERTS))).isTrue();
  }

  @Test
  void transferCapabilitiesFromConfiguration2() {
    SelenideConfig config = new SelenideConfig();
    config.browser(IE);
    DesiredCapabilities commonCapabilities = driverFactory.createCommonCapabilities(config, proxy);
    assertThat(asBool(commonCapabilities.getCapability(ACCEPT_INSECURE_CERTS))).isFalse();
    assertThat(asBool(commonCapabilities.getCapability(ACCEPT_SSL_CERTS))).isTrue();
  }

  private boolean asBool(Object raw) {
    if (raw != null) {
      if (raw instanceof String) {
        return Boolean.parseBoolean((String) raw);
      } else if (raw instanceof Boolean) {
        return (Boolean) raw;
      }
    }
    return false;
  }
}
