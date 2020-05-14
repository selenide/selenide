package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.SelenideDriver;
import org.openqa.selenium.BuildInfo;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.Arrays.asList;

public class WebDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(WebDriverFactory.class);

  protected List<AbstractDriverFactory> factories = asList(
      new RemoteDriverFactory(),
      new ChromeDriverFactory(),
      new LegacyFirefoxDriverFactory(),
      new FirefoxDriverFactory(),
      new EdgeDriverFactory(),
      new InternetExplorerDriverFactory(),
      new OperaDriverFactory()
  );

  protected BrowserResizer browserResizer = new BrowserResizer();

  public WebDriver createWebDriver(Config config, Proxy proxy) {
    log.debug("browser={}", config.browser());
    log.debug("browser.version={}", config.browserVersion());
    log.debug("remote={}", config.remote());
    log.debug("browserSize={}", config.browserSize());
    log.debug("startMaximized={}", config.startMaximized());

    Browser browser = new Browser(config.browser(), config.headless());

    DriverFactory webdriverFactory = factories.stream()
        .filter(factory -> factory.supports(config, browser))
        .findAny()
        .orElseGet(DefaultDriverFactory::new);

    if (config.driverManagerEnabled()) {
      webdriverFactory.setupBinary();
    }

    WebDriver webdriver = webdriverFactory.create(config, browser, proxy);
    browserResizer.adjustBrowserSize(config, webdriver);
    browserResizer.adjustBrowserPosition(config, webdriver);

    logBrowserVersion(webdriver);
    log.info("Selenide v. {}", SelenideDriver.class.getPackage().getImplementationVersion());
    logSeleniumInfo(config);
    return webdriver;
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
