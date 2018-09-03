package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.browserBinary;
import static com.codeborne.selenide.Configuration.chromeSwitches;
import static com.codeborne.selenide.Configuration.headless;

class ChromeDriverFactory extends AbstractDriverFactory {

  private static final Logger log = Logger.getLogger(ChromeDriverFactory.class.getName());

  WebDriver create(final Proxy proxy) {
    ChromeOptions options = createChromeOptions(proxy);
    return new ChromeDriver(options);
  }

  @Override
  boolean supports(Browser browser) {
    return browser.isChrome();
  }

  ChromeOptions createChromeOptions(Proxy proxy) {
    ChromeOptions options = new ChromeOptions();
    options.setHeadless(headless);
    if (!browserBinary.isEmpty()) {
      log.info("Using browser binary: " + browserBinary);
      options.setBinary(browserBinary);
    }
    if (chromeSwitches != null) {
      options.addArguments(chromeSwitches);
    }
    options.merge(createCommonCapabilities(proxy));
    options = transferChromeOptionsFromSystemProperties(options);
    log.config("Chrome options:" + options.toString());
    return options;
  }


  /**
   * This method only handles so-called "arguments" and "preferences"
   * for ChromeOptions (there is also "Extensions" etc.)
   *
   * @param currentChromeOptions
   * @return
   */
  private ChromeOptions transferChromeOptionsFromSystemProperties(ChromeOptions currentChromeOptions) {
    String prefix = "chromeoptions.";
    for (String key : System.getProperties().stringPropertyNames()) {
      if (key.startsWith(prefix)) {
        String capability = key.substring(prefix.length());
        String value = System.getProperties().getProperty(key);
        switch (capability) {
          case "args": {
            List<String> args = Arrays.asList(value.split(","));
            currentChromeOptions.addArguments(args);
            break;
          }
          case "prefs": {
            Map<String, Object> prefs = parsePreferencesFromString(value);
            currentChromeOptions.setExperimentalOption("prefs", prefs);
            break;
          }
          default:
            log.warning(capability + " is ignored." +
                    "Only so-called arguments (chromeoptions.args=<values comma separated>) " +
                    "and preferences (chromeoptions.prefs=<comma-separated dictionary of key=value> " +
                    "are supported for the chromeoptions at the moment");
            break;
        }
      }
    }
    return currentChromeOptions;
  }

  private Map<String, Object> parsePreferencesFromString(String preferencesString) {
    Map<String, Object> prefs = new HashMap<>();
    String[] allPrefs = preferencesString.split(",");
    for (String pref : allPrefs) {
      String[] keyValue = pref.split("=");

      if (keyValue.length == 1) {
        log.warning(String.format(
            "Missing '=' sign while parsing <key=value> pairs from %s. Key '%s' is ignored.",
            preferencesString, keyValue[0]));
        continue;
      } else if (keyValue.length > 2) {
        log.warning(String.format(
            "More than one '=' sign while parsing <key=value> pairs from %s. Key '%s' is ignored.",
            preferencesString, keyValue[0]));
        continue;
      }

      Object prefValue = convertStringToNearestObjectType(keyValue[1]);
      prefs.put(keyValue[0], prefValue);
    }
    return prefs;
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
