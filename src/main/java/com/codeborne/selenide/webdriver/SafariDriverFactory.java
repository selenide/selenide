package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariDriverService;
import org.openqa.selenium.safari.SafariOptions;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@ParametersAreNonnullByDefault
public class SafariDriverFactory extends AbstractDriverFactory {

  @Nonnull
  @CheckReturnValue
  @Override
  public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    SafariDriverService driverService = createDriverService(config);
    SafariOptions capabilities = createCapabilities(config, browser, proxy, browserDownloadsFolder);
    return new SafariDriver(driverService, capabilities);
  }

  private SafariDriverService createDriverService(Config config) {
    return withLog(config, new SafariDriverService.Builder());
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public SafariOptions createCapabilities(Config config, Browser browser,
                                          @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    SafariOptions options = new SafariOptions();
    if (config.headless()) {
      throw new IllegalArgumentException("headless browser not supported in Safari. Set headless property to false.");
    }
    if (isNotEmpty(config.browserBinary())) {
      throw new IllegalArgumentException("browser binary path not supported in Safari. Reset browserBinary setting.");
    }
    return options.merge(createCommonCapabilities(new SafariOptions(), config, browser, proxy));
  }
}
