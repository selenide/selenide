package com.codeborne.selenide.webdriver;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.chromeSwitches;
import static com.codeborne.selenide.WebDriverRunner.CHROME;

class ChromeDriverFactory extends AbstractDriverFactory {

  private static final Logger log = Logger.getLogger(ChromeDriverFactory.class.getName());

  WebDriver create(final Proxy proxy) {
    DesiredCapabilities capabilities = createCommonCapabilities(proxy);
    ChromeOptions options = createChromeOptions();
    capabilities.setCapability(ChromeOptions.CAPABILITY, options);
    return new ChromeDriver(capabilities);
  }

  @Override
  boolean supports() {
    return CHROME.equalsIgnoreCase(browser);
  }

  private ChromeOptions createChromeOptions() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--no-sandbox");  // This make Chromium reachable (?)
    if (chromeSwitches != null) {
      options.addArguments(chromeSwitches);
    }
    options = transferChromeOptionsFromSystemProperties(options, "chromeoptions.");
    try {
      log.config("Chrome options:" + options.toJson().toString());
    } catch (IOException e) {
      log.warning("Error while reading from file:" + e.getMessage() + ". Ignoring it.");
      e.printStackTrace(System.err);
    }
    return options;
  }

  /**
   * This method only handles so-called "arguments" for ChromeOptions (there is also "ExperimentalOptions", "Extensions" etc.)
   *
   * @param currentChromeOptions
   * @param prefix
   * @return
   */
  private ChromeOptions transferChromeOptionsFromSystemProperties(final ChromeOptions currentChromeOptions, final String prefix) {
    for (String key : System.getProperties().stringPropertyNames()) {
      if (key.startsWith(prefix)) {
        String capability = key.substring(prefix.length());
        String value = System.getProperties().getProperty(key);
        if (capability.equals("args")) {
          List<String> args = Arrays.asList(value.split(","));
          currentChromeOptions.addArguments(args);
        } else {
          log.warning(capability + "is ignored." +
                  "Only so-called arguments (chromeoptions.args=<values comma separated>) " +
                  "are supported for the chromeoptions at the moment");
        }
      }
    }
    return currentChromeOptions;
  }
}
