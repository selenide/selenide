package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import java.awt.Toolkit;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.browserPosition;
import static com.codeborne.selenide.Configuration.browserSize;
import static com.codeborne.selenide.Configuration.startMaximized;

class BrowserResizer {
  private static final Logger log = Logger.getLogger(BrowserResizer.class.getName());

  WebDriver adjustBrowserPosition(WebDriver driver) {
    if (browserPosition != null) {
      log.info("Set browser position to " + browserPosition);
      String[] coordinates = browserPosition.split("x");
      int x = Integer.parseInt(coordinates[0]);
      int y = Integer.parseInt(coordinates[1]);
      Point target = new Point(x, y);
      Point current = driver.manage().window().getPosition();
      if (!current.equals(target)) {
        driver.manage().window().setPosition(target);
      }
    }
    return driver;
  }

  WebDriver adjustBrowserSize(Browser browser, WebDriver driver) {
    if (browserSize != null) {
      log.info("Set browser size to " + browserSize);
      String[] dimension = browserSize.split("x");
      int width = Integer.parseInt(dimension[0]);
      int height = Integer.parseInt(dimension[1]);
      driver.manage().window().setSize(new org.openqa.selenium.Dimension(width, height));
    }
    else if (startMaximized) {
      try {
        if (browser.isChrome()) {
          maximizeChromeBrowser(driver.manage().window());
        }
        else {
          driver.manage().window().maximize();
        }
      }
      catch (Exception cannotMaximize) {
        log.warning("Cannot maximize " + driver.getClass().getSimpleName() + ": " + cannotMaximize);
      }
    }
    return driver;
  }

  private void maximizeChromeBrowser(WebDriver.Window window) {
    // Chrome driver does not support maximizing. Let' apply black magic!
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
