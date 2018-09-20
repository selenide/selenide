package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.SelenideDriver;
import org.openqa.selenium.BuildInfo;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.logging.Logger;

import static java.util.Arrays.asList;

public class WebDriverFactory {

  private static final Logger log = Logger.getLogger(WebDriverFactory.class.getName());

  protected List<AbstractDriverFactory> factories = asList(
      new RemoteDriverFactory(),
      new ChromeDriverFactory(),
      new LegacyFirefoxDriverFactory(),
      new FirefoxDriverFactory(),
      new HtmlUnitDriverFactory(),
      new EdgeDriverFactory(),
      new InternetExplorerDriverFactory(),
      new PhantomJsDriverFactory(),
      new OperaDriverFactory(),
      new SafariDriverFactory(),
      new JBrowserDriverFactory()
  );

  protected WebDriverBinaryManager webDriverBinaryManager = new WebDriverBinaryManager();

  protected BrowserResizer browserResizer = new BrowserResizer();

  public WebDriver createWebDriver(Config config, Proxy proxy) {
    log.config("browser=" + config.browser());
    log.config("browser.version=" + config.browserVersion());
    log.config("remote=" + config.remote());
    log.config("browserSize=" + config.browserSize());
    log.config("startMaximized=" + config.startMaximized());

    Browser browser = new Browser(config.browser(), config.headless());

    if (config.driverManagerEnabled() && config.remote() == null) {
      webDriverBinaryManager.setupBinaryPath(browser);
    }

    WebDriver webdriver = factories.stream()
        .filter(factory -> factory.supports(config, browser))
        .findAny()
        .map(driverFactory -> driverFactory.create(config, proxy))
        .orElseGet(() -> new DefaultDriverFactory().create(config, proxy));

    webdriver = browserResizer.adjustBrowserSize(config, browser, webdriver);
    webdriver = browserResizer.adjustBrowserPosition(config, webdriver);

    logBrowserVersion(webdriver);
    log.info("Selenide v. " + SelenideDriver.class.getPackage().getImplementationVersion());
    logSeleniumInfo(config);
    return webdriver;
  }

  protected void logSeleniumInfo(Config config) {
    if (config.remote() == null) {
      BuildInfo seleniumInfo = new BuildInfo();
      log.info(
          "Selenium WebDriver v. " + seleniumInfo.getReleaseLabel() + " build time: " + seleniumInfo.getBuildTime());
    }
  }

  protected void logBrowserVersion(WebDriver webdriver) {
    if (webdriver instanceof HasCapabilities) {
      Capabilities capabilities = ((HasCapabilities) webdriver).getCapabilities();
      log.info("BrowserName=" + capabilities.getBrowserName() +
          " Version=" + capabilities.getVersion() + " Platform=" + capabilities.getPlatform());
    } else {
      log.info("BrowserName=" + webdriver.getClass().getName());
    }
  }
}
