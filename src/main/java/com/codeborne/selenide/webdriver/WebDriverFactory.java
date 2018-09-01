package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Selenide;
import org.openqa.selenium.BuildInfo;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.browserSize;
import static com.codeborne.selenide.Configuration.browserVersion;
import static com.codeborne.selenide.Configuration.driverManagerEnabled;
import static com.codeborne.selenide.Configuration.headless;
import static com.codeborne.selenide.Configuration.remote;
import static com.codeborne.selenide.Configuration.startMaximized;
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

  public WebDriver createWebDriver(Proxy proxy) {
    log.config("Configuration.browser=" + browser);
    log.config("Configuration.browser.version=" + browserVersion);
    log.config("Configuration.remote=" + remote);
    log.config("Configuration.browserSize=" + browserSize);
    log.config("Configuration.startMaximized=" + startMaximized);

    Browser browzer = new Browser(browser, headless);

    if (driverManagerEnabled && remote == null) {
      webDriverBinaryManager.setupBinaryPath(browzer);
    }

    WebDriver webdriver = factories.stream()
        .filter(AbstractDriverFactory::supports)
        .findAny()
        .map(driverFactory -> driverFactory.create(proxy))
        .orElseGet(() -> new DefaultDriverFactory().create(proxy));

    webdriver = browserResizer.adjustBrowserSize(browzer, webdriver);
    webdriver = browserResizer.adjustBrowserPosition(webdriver);

    logBrowserVersion(webdriver);
    log.info("Selenide v. " + Selenide.class.getPackage().getImplementationVersion());
    logSeleniumInfo();
    return webdriver;
  }

  protected void logSeleniumInfo() {
    if (remote == null) {
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
