package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import java.util.logging.Logger;

class BrowserResizer {
  private static final Logger log = Logger.getLogger(BrowserResizer.class.getName());

  WebDriver adjustBrowserPosition(Config config, WebDriver driver) {
    if (config.browserPosition() != null) {
      log.info("Set browser position to " + config.browserPosition());
      String[] coordinates = config.browserPosition().split("x");
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

  WebDriver adjustBrowserSize(Config config, Browser browser, WebDriver driver) {
    if (config.browserSize() != null && !config.startMaximized()) {
      log.info("Set browser size to " + config.browserSize());
      String[] dimension = config.browserSize().split("x");
      int width = Integer.parseInt(dimension[0]);
      int height = Integer.parseInt(dimension[1]);
      driver.manage().window().setSize(new org.openqa.selenium.Dimension(width, height));
    }
    else if (config.startMaximized()) {
      try {
        driver.manage().window().maximize();
      }
      catch (Exception cannotMaximize) {
        log.warning("Cannot maximize " + driver.getClass().getSimpleName() + ": " + cannotMaximize);
      }
    }
    return driver;
  }
}
