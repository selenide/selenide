package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class InternetExplorerDriverFactory extends AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(InternetExplorerDriverFactory.class);

  @Override
  public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    InternetExplorerOptions options = createCapabilities(config, browser, proxy, browserDownloadsFolder);
    return new InternetExplorerDriver(options);
  }

  @Override
  public InternetExplorerOptions createCapabilities(Config config, Browser browser,
                                                    @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    Capabilities capabilities = createCommonCapabilities(new InternetExplorerOptions(), config, browser, proxy);
    InternetExplorerOptions options = new InternetExplorerOptions(capabilities);
    if (isNotEmpty(config.browserBinary())) {
      log.info("Using browser binary: {}", config.browserBinary());
      log.warn("Changing browser binary not supported in InternetExplorer, setting will be ignored.");
    }
    return options;
  }
}
