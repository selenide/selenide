package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.SelenideDriver;
import org.openqa.selenium.BuildInfo;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Browsers.EDGE;
import static com.codeborne.selenide.Browsers.FIREFOX;
import static com.codeborne.selenide.Browsers.IE;
import static com.codeborne.selenide.Browsers.INTERNET_EXPLORER;
import static com.codeborne.selenide.Browsers.LEGACY_FIREFOX;
import static com.codeborne.selenide.Browsers.OPERA;

public class WebDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(WebDriverFactory.class);

  protected Map<String, AbstractDriverFactory> factories = factories();
  protected RemoteDriverFactory remoteDriverFactory = new RemoteDriverFactory();
  protected BrowserResizer browserResizer = new BrowserResizer();

  private Map<String, AbstractDriverFactory> factories() {
    Map<String, AbstractDriverFactory> result = new HashMap<>();
    result.put(CHROME, new ChromeDriverFactory());
    result.put(LEGACY_FIREFOX, new LegacyFirefoxDriverFactory());
    result.put(FIREFOX, new FirefoxDriverFactory());
    result.put(EDGE, new EdgeDriverFactory());
    result.put(INTERNET_EXPLORER, new InternetExplorerDriverFactory());
    result.put(IE, new InternetExplorerDriverFactory());
    result.put(OPERA, new OperaDriverFactory());
    return result;
  }

  public WebDriver createWebDriver(Config config, Proxy proxy) {
    log.debug("browser={}", config.browser());
    log.debug("browser.version={}", config.browserVersion());
    log.debug("remote={}", config.remote());
    log.debug("browserSize={}", config.browserSize());
    log.debug("startMaximized={}", config.startMaximized());

    Browser browser = new Browser(config.browser(), config.headless());
    WebDriver webdriver = createWebDriverInstance(config, proxy, browser);

    browserResizer.adjustBrowserSize(config, webdriver);
    browserResizer.adjustBrowserPosition(config, webdriver);

    logBrowserVersion(webdriver);
    log.info("Selenide v. {}", SelenideDriver.class.getPackage().getImplementationVersion());
    logSeleniumInfo(config);
    return webdriver;
  }

  private WebDriver createWebDriverInstance(Config config, Proxy proxy, Browser browser) {
    DriverFactory webdriverFactory = findFactory(browser);

    if (config.remote() != null) {
      MutableCapabilities capabilities = webdriverFactory.createCapabilities(config, browser, proxy);
      return remoteDriverFactory.create(config, browser, capabilities);
    }
    else {
      if (config.driverManagerEnabled()) {
        webdriverFactory.setupWebdriverBinary();
      }
      return webdriverFactory.create(config, browser, proxy);
    }
  }

  private DriverFactory findFactory(Browser browser) {
    return factories.computeIfAbsent(browser.name.toLowerCase(), browserName -> new DefaultDriverFactory());
  }

  protected void logSeleniumInfo(Config config) {
    if (config.remote() == null) {
      BuildInfo seleniumInfo = new BuildInfo();
      log.info("Selenium WebDriver v. {} build time: {}", seleniumInfo.getReleaseLabel(), seleniumInfo.getBuildTime());
    }
  }

  protected void logBrowserVersion(WebDriver webdriver) {
    if (webdriver instanceof HasCapabilities) {
      Capabilities capabilities = ((HasCapabilities) webdriver).getCapabilities();
      log.info("BrowserName={} Version={} Platform={}",
        capabilities.getBrowserName(), capabilities.getVersion(), capabilities.getPlatform());
    } else {
      log.info("BrowserName={}", webdriver.getClass().getName());
    }
  }
}
