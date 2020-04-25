package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ChromeDriverFactory extends AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(ChromeDriverFactory.class);

  @Override
  WebDriver create(Config config, Proxy proxy) {
    ChromeOptions options = createChromeOptions(config, proxy);
    return new ChromeDriver(options);
  }

  @Override
  boolean supports(Config config, Browser browser) {
    return browser.isChrome();
  }

  ChromeOptions createChromeOptions(Config config, Proxy proxy) {
    ChromeOptions options = new ChromeOptions();
    options.setHeadless(config.headless());
    if (!config.browserBinary().isEmpty()) {
      log.info("Using browser binary: {}", config.browserBinary());
      options.setBinary(config.browserBinary());
    }
    options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation", "load-extension"});
    options.addArguments("--proxy-bypass-list=<-loopback>");
    options.merge(createCommonCapabilities(config, proxy));
    transferChromeOptionsFromSystemProperties(config, options);
    log.debug("Chrome options: {}", options.toString());
    return options;
  }


  /**
   * This method only handles so-called "arguments" and "preferences"
   * for ChromeOptions (there is also "Extensions" etc.)
   */
  private void transferChromeOptionsFromSystemProperties(Config config, ChromeOptions currentChromeOptions) {
    if (System.getProperty("chromeoptions.args") != null) {
      Stream<String> params = Arrays.stream(parseCSVhandlingQuotes(System.getProperty("chromeoptions.args")));
      List<String> args = params
        .map(s -> s.replace("\"", ""))
        .collect(Collectors.toList());
      currentChromeOptions.addArguments(args);
    }

    Map<String, Object> chromePreferences = new HashMap<>();
    chromePreferences.put("credentials_enable_service", false);
    chromePreferences.put("download.default_directory", new File(config.downloadsFolder()).getAbsolutePath());

    if (System.getProperty("chromeoptions.prefs") != null) {
      Map<String, Object> prefs = parsePreferencesFromString(System.getProperty("chromeoptions.prefs"));
      chromePreferences.putAll(prefs);
    }
    currentChromeOptions.setExperimentalOption("prefs", chromePreferences);

    if (System.getProperty("chromeoptions.mobileEmulation") != null) {
      Map<String, Object> prefs = parsePreferencesFromString(System.getProperty("chromeoptions.mobileEmulation"));
      currentChromeOptions.setExperimentalOption("mobileEmulation", prefs);
    }
  }

  private Map<String, Object> parsePreferencesFromString(String preferencesString) {
    Map<String, Object> prefs = new HashMap<>();
    String[] allPrefs = parseCSVhandlingQuotes(preferencesString);
    for (String pref : allPrefs) {
      String[] keyValue = pref
        .replace("\"", "")
        .split("=");

      if (keyValue.length == 1) {
        log.warn("Missing '=' sign while parsing <key=value> pairs from {}. Key '{}' is ignored.",
          preferencesString, keyValue[0]);
        continue;
      } else if (keyValue.length > 2) {
        log.warn("More than one '=' sign while parsing <key=value> pairs from {}. Key '{}' is ignored.",
          preferencesString, keyValue[0]);
        continue;
      }

      Object prefValue = convertStringToNearestObjectType(keyValue[1]);
      prefs.put(keyValue[0], prefValue);
    }
    return prefs;
  }

  /**
   * parse parameters which can come from command-line interface
   * @param csvString comma-separated values, quotes can be used to mask spaces and commas
   *                  Example: 123,"foo bar","bar,foo"
   * @return values as array, quotes are preserved
   */
  private String[] parseCSVhandlingQuotes(String csvString) {
    // Regexp from https://stackoverflow.com/a/15739087/1110503 to handle commas in values
    return csvString.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
  }

  /**
   * Converts String to Boolean\Integer or returns original String.
   * @param value string to convert
   * @return string's object representation
   */
  private Object convertStringToNearestObjectType(String value) {
    switch (value) {
      case "true":
        return true;
      case "false":
        return false;
      default: {
        if (NumberUtils.isParsable(value)) {
          return Integer.parseInt(value);
        }
        return value;
      }
    }
  }
}
