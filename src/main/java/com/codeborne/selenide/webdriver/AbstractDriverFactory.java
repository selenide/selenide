package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_SSL_CERTS;
import static org.openqa.selenium.remote.CapabilityType.PAGE_LOAD_STRATEGY;
import static org.openqa.selenium.remote.CapabilityType.PROXY;

abstract class AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(AbstractDriverFactory.class);

  abstract boolean supports(Config config, Browser browser);

  abstract WebDriver create(Config config, Proxy proxy);

  DesiredCapabilities createCommonCapabilities(Config config, Proxy proxy) {
    DesiredCapabilities browserCapabilities = new DesiredCapabilities();
    if (proxy != null) {
      browserCapabilities.setCapability(PROXY, proxy);
    }
    if (config.browserVersion() != null && !config.browserVersion().isEmpty()) {
      browserCapabilities.setVersion(config.browserVersion());
    }
    browserCapabilities.setCapability(PAGE_LOAD_STRATEGY, config.pageLoadStrategy());
    browserCapabilities.setCapability(ACCEPT_SSL_CERTS, true);

    Browser browser = new Browser(config.browser(), config.headless());
    if (browser.supportsInsecureCerts()) {
      browserCapabilities.setCapability(ACCEPT_INSECURE_CERTS, true);
    }
    transferCapabilitiesFromSystemProperties(browserCapabilities);
    browserCapabilities = mergeCapabilitiesFromConfiguration(config, browserCapabilities);
    return browserCapabilities;
  }

  DesiredCapabilities mergeCapabilitiesFromConfiguration(Config config, DesiredCapabilities currentCapabilities) {
    return currentCapabilities.merge(config.browserCapabilities());
  }

  private void transferCapabilitiesFromSystemProperties(DesiredCapabilities currentBrowserCapabilities) {
    String prefix = "capabilities.";
    for (String key : System.getProperties().stringPropertyNames()) {
      if (key.startsWith(prefix)) {
        String capability = key.substring(prefix.length());
        String value = System.getProperties().getProperty(key);
        log.debug("Use {}={}", key, value);
        if (value.equals("true") || value.equals("false")) {
          currentBrowserCapabilities.setCapability(capability, Boolean.valueOf(value));
        } else if (value.matches("^-?\\d+$")) { //if integer
          currentBrowserCapabilities.setCapability(capability, Integer.parseInt(value));
        } else {
          currentBrowserCapabilities.setCapability(capability, value);
        }
      }
    }
  }
}
