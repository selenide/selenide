package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Config;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

class BrowserResizer {
  private static final Pattern DIMENSION_REGEX = Pattern.compile("-?\\d+x-?\\d+");
  private static final Logger log = LoggerFactory.getLogger(BrowserResizer.class);

  void adjustBrowserPosition(Config config, WebDriver driver) {
    String browserPosition = config.browserPosition();
    if (browserPosition != null) {
      if (!isValidDimension(browserPosition)) {
        throw new IllegalArgumentException(String.format("Browser position %s is incorrect", browserPosition));
      }
      log.info("Set browser position to {}", browserPosition);
      String[] coordinates = browserPosition.split("x");
      int x = Integer.parseInt(coordinates[0]);
      int y = Integer.parseInt(coordinates[1]);
      Point target = new Point(x, y);
      Point current = driver.manage().window().getPosition();
      if (!current.equals(target)) {
        driver.manage().window().setPosition(target);
      }
    }
  }

  void adjustBrowserSize(Config config, WebDriver driver) {
    String browserSize = config.browserSize();
    if (browserSize != null) {
      if (!isValidDimension(browserSize)) {
        throw new IllegalArgumentException(String.format("Browser size %s is incorrect", browserSize));
      }
      log.info("Set browser size to {}", browserSize);
      String[] dimension = browserSize.split("x");
      int width = Integer.parseInt(dimension[0]);
      int height = Integer.parseInt(dimension[1]);
      driver.manage().window().setSize(new org.openqa.selenium.Dimension(width, height));
    }
  }

  static boolean isValidDimension(String dimension) {
    return DIMENSION_REGEX.matcher(dimension).matches();
  }
}
