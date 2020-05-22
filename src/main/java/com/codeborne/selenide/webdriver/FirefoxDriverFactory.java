package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import io.github.bonigarcia.wdm.WebDriverManager;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.nio.charset.StandardCharsets.UTF_8;

public class FirefoxDriverFactory extends AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(FirefoxDriverFactory.class);

  @Override
  public void setupWebdriverBinary() {
    if (isSystemPropertyNotSet("webdriver.gecko.driver")) {
      WebDriverManager.firefoxdriver().setup();
    }
  }

  @Override
  public WebDriver create(Config config, Browser browser, Proxy proxy) {
    String logFilePath = System.getProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
    System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, logFilePath);
    return new FirefoxDriver(createCapabilities(config, browser, proxy));
  }

  @Override
  public FirefoxOptions createCapabilities(Config config, Browser browser, Proxy proxy) {
    FirefoxOptions firefoxOptions = new FirefoxOptions();
    firefoxOptions.setHeadless(config.headless());
    setupBrowserBinary(config, firefoxOptions);
    setupPreferences(firefoxOptions);
    firefoxOptions.merge(createCommonCapabilities(config, browser, proxy));

    setupDownloadsFolder(config, firefoxOptions);

    Map<String, String> ffProfile = collectFirefoxProfileFromSystemProperties();
    if (!ffProfile.isEmpty()) {
      transferFirefoxProfileFromSystemProperties(firefoxOptions, ffProfile);
    }
    return firefoxOptions;
  }

  protected void setupBrowserBinary(Config config, FirefoxOptions firefoxOptions) {
    if (!config.browserBinary().isEmpty()) {
      log.info("Using browser binary: {}", config.browserBinary());
      firefoxOptions.setBinary(config.browserBinary());
    }
  }

  protected void setupPreferences(FirefoxOptions firefoxOptions) {
    firefoxOptions.addPreference("network.automatic-ntlm-auth.trusted-uris", "http://,https://");
    firefoxOptions.addPreference("network.automatic-ntlm-auth.allow-non-fqdn", true);
    firefoxOptions.addPreference("network.negotiate-auth.delegation-uris", "http://,https://");
    firefoxOptions.addPreference("network.negotiate-auth.trusted-uris", "http://,https://");
    firefoxOptions.addPreference("network.http.phishy-userpass-length", 255);
    firefoxOptions.addPreference("security.csp.enable", false);
    firefoxOptions.addPreference("network.proxy.no_proxies_on", "");
    firefoxOptions.addPreference("network.proxy.allow_hijacking_localhost", true);
  }

  protected void setupDownloadsFolder(Config config, FirefoxOptions firefoxOptions) {
    if (config.remote() == null) {
      firefoxOptions.addPreference("browser.download.dir", new File(config.downloadsFolder()).getAbsolutePath());
    }
    firefoxOptions.addPreference("browser.helperApps.neverAsk.saveToDisk", popularContentTypes());
    firefoxOptions.addPreference("pdfjs.disabled", true);  // disable the built-in viewer
    firefoxOptions.addPreference("browser.download.folderList", 2); // 0=Desktop, 1=Downloads, 2="reuse last location"
  }

  protected String popularContentTypes() {
    try {
      return String.join(";", IOUtils.readLines(getClass().getResourceAsStream("/content-types.properties"), UTF_8));
    }
    catch (IOException e) {
      return "text/plain;text/csv;application/zip;application/pdf;application/octet-stream;" +
        "application/msword;application/vnd.ms-excel;text/css;text/html";
    }
  }

  protected Map<String, String> collectFirefoxProfileFromSystemProperties() {
    String prefix = "firefoxprofile.";

    Map<String, String> result = new HashMap<>();
    for (String key : System.getProperties().stringPropertyNames()) {
      if (key.startsWith(prefix)) {
        String capability = key.substring(prefix.length());
        String value = System.getProperties().getProperty(key);
        result.put(capability, value);
      }
    }

    return result;
  }

  protected void transferFirefoxProfileFromSystemProperties(FirefoxOptions firefoxOptions, Map<String, String> ffProfile) {
    FirefoxProfile profile = Optional.ofNullable(firefoxOptions.getProfile()).orElseGet(FirefoxProfile::new);

    for (Map.Entry<String, String> entry : ffProfile.entrySet()) {
      String capability = entry.getKey();
      String value = entry.getValue();
      log.debug("Use {}={}", capability, value);
      setCapability(profile, capability, value);
    }

    firefoxOptions.setProfile(profile);
  }

  protected void setCapability(FirefoxProfile profile, String capability, String value) {
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
