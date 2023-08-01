package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
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
import java.io.File;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;

@ParametersAreNonnullByDefault
public class EdgeDriverFactory extends AbstractChromiumDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(EdgeDriverFactory.class);

  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    EdgeOptions options = createCapabilities(config, browser, proxy, browserDownloadsFolder);
    EdgeDriverService driverService = createDriverService(config);
    return new EdgeDriver(driverService, options);
  }

  private EdgeDriverService createDriverService(Config config) {
    return withLog(config, new EdgeDriverService.Builder());
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public EdgeOptions createCapabilities(Config config, Browser browser,
                                        @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    EdgeOptions options = createCommonCapabilities(new EdgeOptions(), config, browser, proxy);
    options.setCapability(ACCEPT_INSECURE_CERTS, true);
    if (config.headless()) {
      addHeadless(options);
    }

    if (isNotEmpty(config.browserBinary())) {
      log.info("Using browser binary: {}", config.browserBinary());
      options.setBinary(config.browserBinary());
    }

    options.addArguments(createEdgeArguments(config));
    options.setExperimentalOption("prefs", prefs(browserDownloadsFolder, System.getProperty("edgeoptions.prefs", "")));
    return options;
  }

  protected void addHeadless(EdgeOptions options) {
    options.addArguments("--headless=new");
  }

  @CheckReturnValue
  @Nonnull
  protected List<String> createEdgeArguments(Config config) {
    return createChromiumArguments(config, System.getProperty("edgeoptions.args"));
  }
}
