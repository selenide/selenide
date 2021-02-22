package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaDriverService;
import org.openqa.selenium.opera.OperaOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

@ParametersAreNonnullByDefault
public class OperaDriverFactory extends AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(OperaDriverFactory.class);
  private final CdpClient cdpClient = new CdpClient();

  @Override
  public void setupWebdriverBinary() {
    if (isSystemPropertyNotSet("webdriver.opera.driver")) {
      WebDriverManager.operadriver().setup();
    }
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, File browserDownloadsFolder) {
    OperaDriverService driverService = createDriverService(config);
    OperaOptions capabilities = createCapabilities(config, browser, proxy, browserDownloadsFolder);
    OperaDriver driver = new OperaDriver(driverService, capabilities);
    if (browserDownloadsFolder != null) {
      cdpClient.setDownloadsFolder(driverService, driver, browserDownloadsFolder);
    }
    return driver;
  }

  private OperaDriverService createDriverService(Config config) {
    return withLog(config, new OperaDriverService.Builder());
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public OperaOptions createCapabilities(Config config, Browser browser,
                                         @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    OperaOptions operaOptions = new OperaOptions();
    if (config.headless()) {
      throw new InvalidArgumentException("headless browser not supported in Opera. Set headless property to false.");
    }
    if (!config.browserBinary().isEmpty()) {
      log.info("Using browser binary: {}", config.browserBinary());
      operaOptions.setBinary(config.browserBinary());
    }
    operaOptions.merge(createCommonCapabilities(config, browser, proxy));
    return operaOptions;
  }
}
