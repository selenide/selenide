package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.logging.Logger;

class FirefoxDriverFactory extends AbstractDriverFactory {

  private static final Logger log = Logger.getLogger(FirefoxDriverFactory.class.getName());

  @Override
  boolean supports() {
    return WebDriverRunner.isFirefox();
  }

  @Override
  WebDriver create(final Proxy proxy) {
    return createFirefoxDriver(proxy);
  }

  private WebDriver createFirefoxDriver(final Proxy proxy) {
    DesiredCapabilities capabilities = createFirefoxCapabilities(proxy);
    log.info("Firefox 48+ is currently not supported by Selenium Firefox driver. " +
            "Use browser=marionette with geckodriver, when using it.");
    return new FirefoxDriver(capabilities);
  }

  DesiredCapabilities createFirefoxCapabilities(Proxy proxy) {
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
