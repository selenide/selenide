package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;

@ParametersAreNonnullByDefault
public class EdgeDriverFactory extends AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(EdgeDriverFactory.class);
  private static final int FIRST_VERSION_BASED_ON_CHROMIUM = 75;
  private static String browserVersion = null;
  private final CdpClient cdpClient = new CdpClient();

  @Override
  public void setupWebdriverBinary() {
    if (isSystemPropertyNotSet("webdriver.edge.driver")) {
      WebDriverManager manager = WebDriverManager.edgedriver();
      manager.setup();
      browserVersion = manager.getDownloadedVersion();
    }
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy) {
    EdgeOptions options = createCapabilities(config, browser, proxy);
    EdgeDriverService driverService = createDriverService(config);
    EdgeDriver driver = new EdgeDriver(driverService, options);
    if (isChromiumBased()) {
      setDownloadsFolder(config, driverService, driver);
    }
    return driver;
  }

  private void setDownloadsFolder(Config config, EdgeDriverService driverService, EdgeDriver driver) {
    String downloadsFolder = downloadsFolder(config);
    try {
      cdpClient.setDownloadsFolder(driverService, driver, downloadsFolder);
    }
    catch (RuntimeException e) {
      log.error("Failed to set downloads folder to {}", downloadsFolder, e);
    }
  }

  private EdgeDriverService createDriverService(Config config) {
    return new EdgeDriverService.Builder()
      .withLogFile(webdriverLog(config))
      .build();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public EdgeOptions createCapabilities(Config config, Browser browser, @Nullable Proxy proxy) {
    MutableCapabilities capabilities = createCommonCapabilities(config, browser, proxy);
    if (isChromiumBased()) {
      capabilities.setCapability(ACCEPT_INSECURE_CERTS, true);
    }

    EdgeOptions options = new EdgeOptions();
    options.merge(capabilities);
    if (!config.browserBinary().isEmpty()) {
      log.info("Using browser binary: {}", config.browserBinary());
      log.warn("Changing browser binary not supported in Edge, setting will be ignored.");
    }

    return options;
  }

  @CheckReturnValue
  private boolean isChromiumBased() {
    return browserVersion == null || majorVersion(browserVersion) >= FIRST_VERSION_BASED_ON_CHROMIUM;
  }
}
