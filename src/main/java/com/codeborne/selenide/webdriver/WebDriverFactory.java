package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.SelenideDriver;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.BuildInfo;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Browsers.EDGE;
import static com.codeborne.selenide.Browsers.FIREFOX;
import static com.codeborne.selenide.Browsers.IE;
import static com.codeborne.selenide.Browsers.INTERNET_EXPLORER;
import static com.codeborne.selenide.Browsers.SAFARI;

public class WebDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(WebDriverFactory.class);

  private final Map<String, Class<? extends AbstractDriverFactory>> factories = factories();
  private final RemoteDriverFactory remoteDriverFactory = new RemoteDriverFactory();
  private final BrowserResizer browserResizer = new BrowserResizer();

  private Map<String, Class<? extends AbstractDriverFactory>> factories() {
    Map<String, Class<? extends AbstractDriverFactory>> result = new HashMap<>();
    result.put(CHROME, ChromeDriverFactory.class);
    result.put(FIREFOX, FirefoxDriverFactory.class);
    result.put(EDGE, EdgeDriverFactory.class);
    result.put(INTERNET_EXPLORER, InternetExplorerDriverFactory.class);
    result.put(IE, InternetExplorerDriverFactory.class);
    result.put(SAFARI, SafariDriverFactory.class);
    return result;
  }

  public WebDriver createWebDriver(Config config, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    log.debug("browser={}", config.browser());
    log.debug("browser.version={}", config.browserVersion());
    log.debug("remote={}", config.remote());
    log.debug("browserSize={}", config.browserSize());
    if (browserDownloadsFolder != null) {
      log.debug("downloadsFolder={}", browserDownloadsFolder.getAbsolutePath());
    }

    Browser browser = new Browser(config.browser(), config.headless());
    WebDriver webdriver = createWebDriverInstance(config, browser, proxy, browserDownloadsFolder);
    if (needBrowserResize(webdriver)) {
      browserResizer.adjustBrowserSize(config, webdriver);
    }
    browserResizer.adjustBrowserPosition(config, webdriver);
    setLoadTimeout(config, webdriver);

    logVersions(webdriver);
    return webdriver;
  }

  private boolean needBrowserResize(WebDriver webdriver) {
    String browserName = "";
    if (webdriver instanceof HasCapabilities hasCapabilities) {
      Capabilities capabilities = hasCapabilities.getCapabilities();
      browserName = capabilities.getBrowserName();
    }
    Browser browser = new Browser(browserName, false);
    return !browser.isChromium() && !"msedge".equals(browserName);
  }

  private void setLoadTimeout(Config config, WebDriver webdriver) {
    if (config.pageLoadTimeout() < 0) {
      return;
    }
    try {
      webdriver.manage().timeouts().pageLoadTimeout(Duration.ofMillis(config.pageLoadTimeout()));
    }
    catch (UnsupportedCommandException e) {
      log.info("Failed to set page load timeout to {} ms: {}", config.pageLoadTimeout(), e.toString());
    }
    catch (RuntimeException e) {
      log.error("Failed to set page load timeout to {} ms", config.pageLoadTimeout(), e);
    }
  }

  private WebDriver createWebDriverInstance(Config config, Browser browser,
                                            @Nullable Proxy proxy,
                                            @Nullable File browserDownloadsFolder) {
    DriverFactory webdriverFactory = findFactory(browser);

    if (config.remote() != null) {
      MutableCapabilities capabilities = webdriverFactory.createCapabilities(config, browser, proxy, browserDownloadsFolder);
      return remoteDriverFactory.create(config, capabilities);
    }
    else {
      return webdriverFactory.create(config, browser, proxy, browserDownloadsFolder);
    }
  }

  private DriverFactory findFactory(Browser browser) {
    Class<? extends AbstractDriverFactory> factoryClass = factories.getOrDefault(
      browser.name.toLowerCase(Locale.ROOT), DefaultDriverFactory.class);
    try {
      return factoryClass.getConstructor().newInstance();
    }
    catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new RuntimeException("Failed to initialize " + factoryClass.getName(), e);
    }
  }

  private void logVersions(WebDriver webdriver) {
    BuildInfo seleniumInfo = new BuildInfo();
    log.info("Selenide {} / Selenium {} / {}",
      SelenideDriver.class.getPackage().getImplementationVersion(),
      seleniumInfo.getReleaseLabel(),
      getBrowserVersion(webdriver)
    );
  }

  private String getBrowserVersion(WebDriver webdriver) {
    if (webdriver instanceof HasCapabilities hasCapabilities) {
      Capabilities c = hasCapabilities.getCapabilities();
      return "%s %s %s".formatted(c.getBrowserName(), c.getBrowserVersion(), c.getPlatformName());
    } else {
      return webdriver.getClass().getName();
    }
  }
}
