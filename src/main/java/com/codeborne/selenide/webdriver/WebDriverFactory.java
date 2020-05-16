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

import java.util.List;

import static java.util.Arrays.asList;

public class WebDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(WebDriverFactory.class);

  protected List<AbstractDriverFactory> factories = asList(
      new ChromeDriverFactory(),
      new LegacyFirefoxDriverFactory(),
      new FirefoxDriverFactory(),
      new EdgeDriverFactory(),
      new InternetExplorerDriverFactory(),
      new OperaDriverFactory()
  );
  protected RemoteDriverFactory remoteDriverFactory = new RemoteDriverFactory();
  protected BrowserResizer browserResizer = new BrowserResizer();

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
    DriverFactory webdriverFactory = findFactory(config, browser);

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

  private DriverFactory findFactory(Config config, Browser browser) {
    return factories.stream()
          .filter(factory -> factory.supports(config, browser))
          .findAny()
          .orElseGet(DefaultDriverFactory::new);
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
