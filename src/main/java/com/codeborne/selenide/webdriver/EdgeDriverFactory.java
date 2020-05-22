package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;

public class EdgeDriverFactory extends AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(EdgeDriverFactory.class);
  private static final int FIRST_VERSION_BASED_ON_CHROMIUM = 75;
  private String browserVersion = null;

  @Override
  public void setupWebdriverBinary() {
    if (isSystemPropertyNotSet("webdriver.edge.driver")) {
      WebDriverManager manager = WebDriverManager.edgedriver();
      manager.setup();
      browserVersion = manager.getDownloadedVersion();
    }
  }

  @Override
  public WebDriver create(Config config, Browser browser, Proxy proxy) {
    EdgeOptions options = createCapabilities(config, browser, proxy);
    return new EdgeDriver(options);
  }

  @Override
  public EdgeOptions createCapabilities(Config config, Browser browser, Proxy proxy) {
    MutableCapabilities capabilities = createCommonCapabilities(config, browser, proxy);
    if (browserVersion != null && majorVersion(browserVersion) >= FIRST_VERSION_BASED_ON_CHROMIUM) {
      capabilities.setCapability(ACCEPT_INSECURE_CERTS, true);
    }

    EdgeOptions options = new EdgeOptions();
    options.merge(capabilities);
    if (!config.browserBinary().isEmpty()) {
      log.info("Using browser binary: {}", config.browserBinary());
      log.warn("Changing browser binary not supported in Edge, setting will be ignored.");
    }
    return options;
  }
}
