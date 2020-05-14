package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.nio.charset.StandardCharsets.UTF_8;

public class FirefoxDriverFactory extends AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(FirefoxDriverFactory.class);

  @Override
  boolean supports(Config config, Browser browser) {
    return browser.isFirefox();
  }

  @Override
  public WebDriver create(Config config, Proxy proxy) {
    String logFilePath = System.getProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
    System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, logFilePath);
    return createFirefoxDriver(config, proxy);
  }

  private WebDriver createFirefoxDriver(Config config, Proxy proxy) {
    FirefoxOptions options = createFirefoxOptions(config, proxy);
    return new FirefoxDriver(options);
  }

  FirefoxOptions createFirefoxOptions(Config config, Proxy proxy) {
    FirefoxOptions firefoxOptions = new FirefoxOptions();
    firefoxOptions.setHeadless(config.headless());
    if (!config.browserBinary().isEmpty()) {
      log.info("Using browser binary: {}", config.browserBinary());
      firefoxOptions.setBinary(config.browserBinary());
    }
    firefoxOptions.addPreference("network.automatic-ntlm-auth.trusted-uris", "http://,https://");
    firefoxOptions.addPreference("network.automatic-ntlm-auth.allow-non-fqdn", true);
    firefoxOptions.addPreference("network.negotiate-auth.delegation-uris", "http://,https://");
    firefoxOptions.addPreference("network.negotiate-auth.trusted-uris", "http://,https://");
    firefoxOptions.addPreference("network.http.phishy-userpass-length", 255);
    firefoxOptions.addPreference("security.csp.enable", false);
    firefoxOptions.addPreference("network.proxy.no_proxies_on", "");
    firefoxOptions.addPreference("network.proxy.allow_hijacking_localhost", true);

    firefoxOptions.merge(createCommonCapabilities(config, proxy));

    FirefoxProfile profile = Optional.ofNullable(firefoxOptions.getProfile()).orElseGet(FirefoxProfile::new);
    setupDownloadsFolder(config, profile);
    transferFirefoxProfileFromSystemProperties(profile);
    firefoxOptions.setProfile(profile);

    return firefoxOptions;
  }

  private void setupDownloadsFolder(Config config, FirefoxProfile profile) {
    if (profile.getStringPreference("browser.download.dir", "").isEmpty()) {
      profile.setPreference("browser.download.dir", new File(config.downloadsFolder()).getAbsolutePath());
      profile.setPreference("browser.helperApps.neverAsk.saveToDisk", popularContentTypes());
      profile.setPreference("pdfjs.disabled", true);  // disable the built-in viewer
      profile.setPreference("browser.download.folderList", 2); // 0=Desktop, 1=Downloads, 2="reuse last location"
    }
  }

  String popularContentTypes() {
    try {
      return String.join(";", IOUtils.readLines(getClass().getResourceAsStream("/content-types.properties"), UTF_8));
    }
    catch (IOException e) {
      return "text/plain;text/csv;application/zip;application/pdf;application/octet-stream;" +
        "application/msword;application/vnd.ms-excel;text/css;text/html";
    }
  }

  private void transferFirefoxProfileFromSystemProperties(FirefoxProfile profile) {
    String prefix = "firefoxprofile.";

    for (String key : System.getProperties().stringPropertyNames()) {
      if (key.startsWith(prefix)) {
        String capability = key.substring(prefix.length());
        String value = System.getProperties().getProperty(key);
        log.debug("Use {}={}", key, value);
        if (isBoolean(value)) {
          profile.setPreference(capability, parseBoolean(value));
        }
        else if (isInteger(value)) {
          profile.setPreference(capability, parseInt(value));
        }
        else {
          profile.setPreference(capability, value);
        }
      }
    }
  }
}
