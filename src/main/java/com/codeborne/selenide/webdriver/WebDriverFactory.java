package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Selenide;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.BuildInfo;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static com.codeborne.selenide.impl.Describe.describe;
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

  public WebDriver createWebDriver(Proxy proxy) {
    log.config("Configuration.browser=" + browser);
    log.config("Configuration.browser.version=" + browserVersion);
    log.config("Configuration.remote=" + remote);
    log.config("Configuration.browserSize=" + browserSize);
    log.config("Configuration.startMaximized=" + startMaximized);

    if (driverManagerEnabled && remote == null) {
      webDriverBinaryManager.setupBinaryPath();
    }

    WebDriver webdriver = factories.stream()
        .filter(AbstractDriverFactory::supports)
        .findAny()
        .map(driverFactory -> driverFactory.create(proxy))
        .orElseGet(() -> new DefaultDriverFactory().create(proxy));

    webdriver = adjustBrowserSize(webdriver);

    logBrowserVersion(webdriver);
    log.info("Selenide v. " + Selenide.class.getPackage().getImplementationVersion());
    logSeleniumInfo();
    return webdriver;
  }

  protected WebDriver adjustBrowserSize(WebDriver driver) {
    if (browserSize != null) {
      log.info("Set browser size to " + browserSize);
      String[] dimension = browserSize.split("x");
      int width = Integer.parseInt(dimension[0]);
      int height = Integer.parseInt(dimension[1]);
      driver.manage().window().setSize(new org.openqa.selenium.Dimension(width, height));
    } else if (startMaximized) {
      try {
        if (isChrome()) {
          maximizeChromeBrowser(driver.manage().window());
        } else {
          driver.manage().window().maximize();
        }
      } catch (Exception cannotMaximize) {
        log.warning("Cannot maximize " + describe(driver) + ": " + cannotMaximize);
      }
    }
    return driver;
  }

  protected void maximizeChromeBrowser(WebDriver.Window window) {
    // Chrome driver does not yet support maximizing. Let' apply black magic!
    org.openqa.selenium.Dimension screenResolution = getScreenSize();

    window.setSize(screenResolution);
    window.setPosition(new org.openqa.selenium.Point(0, 0));
  }

  Dimension getScreenSize() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();

    return new Dimension(
        (int) toolkit.getScreenSize().getWidth(),
        (int) toolkit.getScreenSize().getHeight());
  }

  void logSeleniumInfo() {
    if (remote == null) {
      BuildInfo seleniumInfo = new BuildInfo();
      log.info(
          "Selenium WebDriver v. " + seleniumInfo.getReleaseLabel() + " build time: " + seleniumInfo
              .getBuildTime());
    }
  }

  void logBrowserVersion(WebDriver webdriver) {
    if (!isHeadless()) {
      Capabilities capabilities = ((RemoteWebDriver) webdriver).getCapabilities();
      log.info(
          "BrowserName=" + capabilities.getBrowserName() + " Version=" + capabilities.getVersion()
              + " Platform=" + capabilities.getPlatform());
    }
  }
}
