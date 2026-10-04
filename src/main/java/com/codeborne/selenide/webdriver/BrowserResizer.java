package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Config;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

class BrowserResizer {
  private static final Pattern DIMENSION_REGEX = Pattern.compile("(-?\\d+)x(-?\\d+)");
  private static final Logger log = LoggerFactory.getLogger(BrowserResizer.class);

  static void adjustBrowserPosition(Config config, WebDriver driver) {
    String browserPosition = config.browserPosition();
    if (browserPosition != null) {
      log.info("Set browser position to {}", browserPosition);
      Point target = parsePosition(browserPosition);
      Point current = driver.manage().window().getPosition();
      if (!current.equals(target)) {
        driver.manage().window().setPosition(target);
      }
    }
  }

  static void adjustBrowserSize(Config config, WebDriver driver) {
    String browserSize = config.browserSize();
    if (browserSize != null) {
      log.info("Set browser size to {}", browserSize);
      Dimension dimension = parseSize(browserSize);
      driver.manage().window().setSize(dimension);
    }
  }

  static boolean isValidDimension(String dimension) {
    return DIMENSION_REGEX.matcher(dimension).matches();
  }

  static Dimension parseSize(String size) {
    int[] wh = parse("browser size", size);
    return new Dimension(wh[0], wh[1]);
  }

  static Point parsePosition(String position) {
    int[] xy = parse("browser position", position);
    return new Point(xy[0], xy[1]);
  }

  private static int[] parse(String name, String dimension) {
    Matcher matcher = DIMENSION_REGEX.matcher(dimension);
    if (!matcher.matches()) {
      throw new IllegalArgumentException(String.format("Invalid %s: \"%s\". Expected format: \"300x200\".", name, dimension));
    }
    return new int[] {parseInt(matcher.replaceFirst("$1")), parseInt(matcher.group(2))};
  }
}
