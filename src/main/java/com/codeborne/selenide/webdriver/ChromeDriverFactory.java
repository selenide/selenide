package com.codeborne.selenide.webdriver;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.WebDriverRunner.CHROME;

class ChromeDriverFactory extends AbstractDriverFactory {

  private static final Logger log = Logger.getLogger(ChromeDriverFactory.class.getName());

  WebDriver create(final Proxy proxy) {
    ChromeOptions options = createChromeOptions(proxy);
    return new ChromeDriver(options);
  }

  @Override
  boolean supports() {
    return CHROME.equalsIgnoreCase(browser);
  }

  ChromeOptions createChromeOptions(Proxy proxy) {
    ChromeOptions options = new ChromeOptions();
    options.setHeadless(headless);
    if (!browserBinary.isEmpty()) {
      options.setBinary(browserBinary);
    }
    options.addArguments("--no-sandbox");  // This make Chromium reachable (?)
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
            Map<String, Object> prefs = new HashMap<>();
            String[] allPrefs = value.split(",");
            for (String pref: allPrefs) {
              String[] keyValue = pref.split("=");
              if (keyValue.length == 1) {
                log.warning("Missing '=' sign while parsing <key=value> pairs from "
                        + capability + ". Key '" + keyValue[0] + "' is ignored.");
                continue;
              }
              else if (keyValue.length > 2) {
                log.warning("More than one '=' sign while parsing <key=value> pairs from "
                        + capability + ". Key '" + keyValue[0] + "' is ignored.");
                continue;
              }
              prefs.put(keyValue[0], parseString(keyValue[1]));
            }
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

  /**
   * prefs Map <String, Object> can contain Int/Boolean/Strings as value
   * we parse "true/false" to boolean, numbers to int, and the rest stays string
   * @param value
   * @return
   */
  private Object parseString(String value) {
    if (value.equals("true")) {

      return true;
    }
    if (value.equals("false")) {

      return false;
    }
    try {

      return Integer.parseInt(value);
    }
    catch (NumberFormatException ignore) {

      return value;
    }
  }
}
