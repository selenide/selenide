package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

import static com.codeborne.selenide.Browsers.EDGE;
import static com.codeborne.selenide.Browsers.IE;
import static com.codeborne.selenide.Browsers.INTERNET_EXPLORER;
import static org.mockito.Mockito.mock;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_SSL_CERTS;
import static org.openqa.selenium.remote.CapabilityType.PAGE_LOAD_STRATEGY;

final class CommonCapabilitiesTest implements WithAssertions {
  private final AbstractDriverFactory driverFactory = new DummyDriverFactory();
  private final Proxy proxy = mock(Proxy.class);

  @Test
  void transferCapabilitiesFromConfiguration() {
    SelenideConfig config = new SelenideConfig();
    config.pageLoadStrategy("foo");
    Capabilities commonCapabilities = driverFactory.createCommonCapabilities(config, browser(config), proxy);
    assertThat(asBool(commonCapabilities.getCapability(ACCEPT_INSECURE_CERTS))).isTrue();
    assertThat(asBool(commonCapabilities.getCapability(ACCEPT_SSL_CERTS))).isTrue();
    assertThat(commonCapabilities.getCapability(PAGE_LOAD_STRATEGY)).isEqualTo(config.pageLoadStrategy());
  }

  @Test
  void transferCapabilitiesFromConfigurationInternetExplorer() {
    SelenideConfig config = new SelenideConfig();
    config.browser(INTERNET_EXPLORER);
    Capabilities commonCapabilities = driverFactory.createCommonCapabilities(config, browser(config), proxy);
    assertThat(asBool(commonCapabilities.getCapability(ACCEPT_INSECURE_CERTS))).isFalse();
    assertThat(asBool(commonCapabilities.getCapability(ACCEPT_SSL_CERTS))).isTrue();
  }

  @Test
  void transferCapabilitiesFromConfigurationIE() {
    SelenideConfig config = new SelenideConfig();
    config.browser(IE);
    Capabilities commonCapabilities = driverFactory.createCommonCapabilities(config, browser(config), proxy);
    assertThat(asBool(commonCapabilities.getCapability(ACCEPT_INSECURE_CERTS))).isFalse();
    assertThat(asBool(commonCapabilities.getCapability(ACCEPT_SSL_CERTS))).isTrue();
  }

  @Test
  void transferCapabilitiesFromConfigurationEdge() {
    SelenideConfig config = new SelenideConfig();
    config.browser(EDGE);
    Capabilities commonCapabilities = driverFactory.createCommonCapabilities(config, browser(config), proxy);
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

  private Browser browser(SelenideConfig config) {
    return new Browser(config.browser(), config.headless());
  }

  @ParametersAreNonnullByDefault
  private static class DummyDriverFactory extends AbstractDriverFactory {
    @Override
    public void setupWebdriverBinary() {
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public MutableCapabilities createCapabilities(Config config, Browser browser, @Nullable Proxy proxy, File browserDownloadsFolder) {
      return new DesiredCapabilities();
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, File browserDownloadsFolder) {
      return mock(WebDriver.class);
    }
  }
}
