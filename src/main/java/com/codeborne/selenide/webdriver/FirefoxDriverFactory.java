package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.browserBinary;
import static com.codeborne.selenide.Configuration.headless;

class FirefoxDriverFactory extends AbstractDriverFactory {

  private static final Logger log = Logger.getLogger(FirefoxDriverFactory.class.getName());

  @Override
  boolean supports(Browser browser) {
    return browser.isFirefox();
  }

  @Override
  WebDriver create(final Proxy proxy) {
    String logFilePath = System.getProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
    System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, logFilePath);
    return createFirefoxDriver(proxy);
  }

  private WebDriver createFirefoxDriver(final Proxy proxy) {
    FirefoxOptions options = createFirefoxOptions(proxy);
    return new FirefoxDriver(options);
  }

  FirefoxOptions createFirefoxOptions(Proxy proxy) {
    FirefoxOptions firefoxOptions = new FirefoxOptions();
    firefoxOptions.setHeadless(headless);
    if (!browserBinary.isEmpty()) {
      log.info("Using browser binary: " + browserBinary);
      firefoxOptions.setBinary(browserBinary);
    }
    firefoxOptions.addPreference("network.automatic-ntlm-auth.trusted-uris", "http://,https://");
    firefoxOptions.addPreference("network.automatic-ntlm-auth.allow-non-fqdn", true);
    firefoxOptions.addPreference("network.negotiate-auth.delegation-uris", "http://,https://");
    firefoxOptions.addPreference("network.negotiate-auth.trusted-uris", "http://,https://");
    firefoxOptions.addPreference("network.http.phishy-userpass-length", 255);
    firefoxOptions.addPreference("security.csp.enable", false);
    firefoxOptions.addPreference("network.proxy.no_proxies_on", "");

    firefoxOptions.merge(createCommonCapabilities(proxy));
    firefoxOptions = transferFirefoxProfileFromSystemProperties(firefoxOptions);

    return firefoxOptions;
  }

  private FirefoxOptions transferFirefoxProfileFromSystemProperties(FirefoxOptions currentFirefoxOptions) {
    String prefix = "firefoxprofile.";
    FirefoxProfile profile = new FirefoxProfile();
    for (String key : System.getProperties().stringPropertyNames()) {
      if (key.startsWith(prefix)) {
        String capability = key.substring(prefix.length());
        String value = System.getProperties().getProperty(key);
        log.config("Use " + key + "=" + value);
        if (value.equals("true") || value.equals("false")) {
          profile.setPreference(capability, Boolean.valueOf(value));
        }
        else if (value.matches("^-?\\d+$")) { //if integer
          profile.setPreference(capability, Integer.parseInt(value));
        }
        else {
          profile.setPreference(capability, value);
        }
      }
    }
    return currentFirefoxOptions.setProfile(profile);
  }
}
