package com.codeborne.selenide.webdriver;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Arrays;
import java.util.List;
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
   * This method only handles so-called "arguments" for ChromeOptions (there is also "ExperimentalOptions", "Extensions" etc.)
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
