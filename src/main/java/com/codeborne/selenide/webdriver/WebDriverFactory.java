package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Selenide;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.BuildInfo;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.*;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static com.codeborne.selenide.impl.Describe.describe;

public class WebDriverFactory {
  private static final Logger log = Logger.getLogger(WebDriverFactory.class.getName());

  private final DriverProcessor processor = new RemoteDriverProcessor();

  public WebDriver createWebDriver(Proxy proxy) {
    log.config("Configuration.browser=" + browser);
    log.config("Configuration.browser.version=" + browserVersion);
    log.config("Configuration.remote=" + remote);
    log.config("Configuration.browserSize=" + browserSize);
    log.config("Configuration.startMaximized=" + startMaximized);

    WebDriver webdriver = processor.process(proxy);

    webdriver = adjustBrowserSize(webdriver);

    if (!isHeadless()) {
      Capabilities capabilities = ((RemoteWebDriver) webdriver).getCapabilities();
      log.info("BrowserName=" + capabilities.getBrowserName() + " Version=" + capabilities.getVersion()
              + " Platform=" + capabilities.getPlatform());
    }
    log.info("Selenide v. " + Selenide.class.getPackage().getImplementationVersion());
    if (remote == null) {
      BuildInfo seleniumInfo = new BuildInfo();
      log.info("Selenium WebDriver v. " + seleniumInfo.getReleaseLabel() + " build time: " + seleniumInfo.getBuildTime());
    } else {
      ((RemoteWebDriver) webdriver).setFileDetector(new LocalFileDetector());
    }

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
}
