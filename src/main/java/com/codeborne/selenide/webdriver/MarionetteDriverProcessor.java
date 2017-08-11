package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.function.Supplier;
import java.util.logging.Logger;

class MarionetteDriverProcessor extends DriverProcessor {

  private static final Logger log = Logger.getLogger(MarionetteDriverProcessor.class.getName());

  private final Supplier<Boolean> condition = WebDriverRunner::isMarionette;
  private final DriverProcessor nextProcessor;

  MarionetteDriverProcessor() {
    this.nextProcessor = new FirefoxDriverProcessor();
  }

  @Override
  WebDriver process(final Proxy proxy) {
    return condition.get() ? createMarionetteDriver(proxy) : nextProcessor.process(proxy);
  }

  private WebDriver createMarionetteDriver(final Proxy proxy) {
    DesiredCapabilities capabilities = createFirefoxCapabilities(proxy);
    capabilities.setCapability("marionette", true);
    return new FirefoxDriver(capabilities);
  }

  private DesiredCapabilities createFirefoxCapabilities(Proxy proxy) {
    FirefoxProfile myProfile = new FirefoxProfile();
    myProfile.setPreference("network.automatic-ntlm-auth.trusted-uris", "http://,https://");
    myProfile.setPreference("network.automatic-ntlm-auth.allow-non-fqdn", true);
    myProfile.setPreference("network.negotiate-auth.delegation-uris", "http://,https://");
    myProfile.setPreference("network.negotiate-auth.trusted-uris", "http://,https://");
    myProfile.setPreference("network.http.phishy-userpass-length", 255);
    myProfile.setPreference("security.csp.enable", false);

    DesiredCapabilities capabilities = createCommonCapabilities(proxy);
    myProfile = transferFirefoxProfileFromSystemProperties(myProfile, "firefoxprofile.");
    capabilities.setCapability("marionette", false);
    capabilities.setCapability(FirefoxDriver.PROFILE, myProfile);
    return capabilities;
  }

  private FirefoxProfile transferFirefoxProfileFromSystemProperties(FirefoxProfile currentFirefoxProfile, String prefix) {
    for (String key : System.getProperties().stringPropertyNames()) {
      if (key.startsWith(prefix)) {
        String capability = key.substring(prefix.length());
        String value = System.getProperties().getProperty(key);
        log.config("Use " + key + "=" + value);
        if (value.equals("true") || value.equals("false")) {
          currentFirefoxProfile.setPreference(capability, Boolean.valueOf(value));
        } else if (value.matches("^-?\\d+$")) { //if integer
          currentFirefoxProfile.setPreference(capability, Integer.parseInt(value));
        } else {
          currentFirefoxProfile.setPreference(capability, value);
        }
      }
    }
    return currentFirefoxProfile;
  }
}
