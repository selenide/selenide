package com.codeborne.selenide.appium.commands;

import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * The area where scroll/swipe gestures are performed: either the given container element, or the whole screen.
 */
final class GestureArea {
  private GestureArea() {
  }

  static Rectangle of(WebDriver appiumDriver, @Nullable WebElement container) {
    return container != null ?
      container.getRect() :
      new Rectangle(new Point(0, 0), appiumDriver.manage().window().getSize());
  }
}
