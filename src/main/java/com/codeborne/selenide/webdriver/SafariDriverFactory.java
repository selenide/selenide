package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.InvalidArgumentException;
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

@ParametersAreNonnullByDefault
public class SafariDriverFactory extends AbstractDriverFactory {
  @Override
  public void setupWebdriverBinary() {
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, File browserDownloadsFolder) {
    SafariDriverService driverService = createDriverService(config);
    SafariOptions capabilities = createCapabilities(config, browser, proxy, browserDownloadsFolder);
    return new SafariDriver(driverService, capabilities);
  }

  private SafariDriverService createDriverService(Config config) {
    return withLog(config, new SafariDriverService.Builder().usingTechnologyPreview(false));
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public SafariOptions createCapabilities(Config config, Browser browser,
                                          @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    SafariOptions options = new SafariOptions();
    if (config.headless()) {
      throw new InvalidArgumentException("headless browser not supported in Safari. Set headless property to false.");
    }
    if (!config.browserBinary().isEmpty()) {
      throw new InvalidArgumentException("browser binary path not supported in Safari. Reset browserBinary setting.");
    }
    options.merge(createCommonCapabilities(config, browser, proxy));
    return options;
  }
}
