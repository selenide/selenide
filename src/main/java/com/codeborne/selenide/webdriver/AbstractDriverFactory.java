package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_SSL_CERTS;
import static org.openqa.selenium.remote.CapabilityType.PAGE_LOAD_STRATEGY;
import static org.openqa.selenium.remote.CapabilityType.PROXY;
import static org.openqa.selenium.remote.CapabilityType.SUPPORTS_ALERTS;
import static org.openqa.selenium.remote.CapabilityType.TAKES_SCREENSHOT;

abstract class AbstractDriverFactory implements DriverFactory {
  private static final Logger log = LoggerFactory.getLogger(AbstractDriverFactory.class);
  private static final Pattern REGEX_SIGNED_INTEGER = Pattern.compile("^-?\\d+$");

  abstract boolean supports(Config config, Browser browser);

  protected MutableCapabilities createCommonCapabilities(Config config, Browser browser, Proxy proxy) {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    if (proxy != null) {
      capabilities.setCapability(PROXY, proxy);
    }
    if (config.browserVersion() != null && !config.browserVersion().isEmpty()) {
      capabilities.setVersion(config.browserVersion());
    }
    capabilities.setCapability(PAGE_LOAD_STRATEGY, config.pageLoadStrategy());
    capabilities.setCapability(ACCEPT_SSL_CERTS, true);

    if (browser.supportsInsecureCerts()) {
      capabilities.setCapability(ACCEPT_INSECURE_CERTS, true);
    }
    capabilities.setJavascriptEnabled(true);
    capabilities.setCapability(TAKES_SCREENSHOT, true);
    capabilities.setCapability(SUPPORTS_ALERTS, true);

    transferCapabilitiesFromSystemProperties(capabilities);
    return new MergeableCapabilities(capabilities, config.browserCapabilities());
  }

  protected void transferCapabilitiesFromSystemProperties(DesiredCapabilities currentBrowserCapabilities) {
    String prefix = "capabilities.";
    for (String key : System.getProperties().stringPropertyNames()) {
      if (key.startsWith(prefix)) {
        String capability = key.substring(prefix.length());
        String value = System.getProperties().getProperty(key);
        log.debug("Use {}={}", key, value);
        currentBrowserCapabilities.setCapability(capability, convertStringToNearestObjectType(value));
      }
    }
  }

  /**
   * Converts String to Boolean\Integer or returns original String.
   * @param value string to convert
   * @return string's object representation
   */
  protected Object convertStringToNearestObjectType(String value) {
    if (isBoolean(value)) {
      return Boolean.valueOf(value);
    }
    else if (isInteger(value)) {
      return Integer.parseInt(value);
    }
    else {
      return value;
    }
  }

  protected boolean isInteger(String value) {
    return REGEX_SIGNED_INTEGER.matcher(value).matches();
  }

  protected boolean isBoolean(String value) {
    return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
  }

  protected boolean isSystemPropertyNotSet(String key) {
    return isBlank(System.getProperty(key, ""));
  }
}
