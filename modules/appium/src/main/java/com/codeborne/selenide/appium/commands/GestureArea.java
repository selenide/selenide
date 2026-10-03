package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.appium.AppiumScrollCoordinates;
import com.codeborne.selenide.appium.AppiumSwipeDirection;
import com.codeborne.selenide.appium.ScrollDirection;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.appium.AppiumSwipeDirection.RIGHT;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * The area where scroll/swipe gestures are performed: either the visible part of the given container element,
 * or the whole screen.
 */
final class GestureArea {
  private static final float SWIPE_LEFT_POINT_WIDTH_PERCENT = 0.25f;
  private static final float SWIPE_RIGHT_POINT_WIDTH_PERCENT = 0.75f;

  private final Rectangle area;

  GestureArea(Rectangle area) {
    this.area = area;
  }

  static GestureArea of(WebDriver appiumDriver, @Nullable WebElement container) {
    Dimension screen = appiumDriver.manage().window().getSize();
    return container != null ?
      visiblePart(container.getRect(), screen) :
      new GestureArea(new Rectangle(new Point(0, 0), screen));
  }

  /**
   * Cuts off the parts of the container that are outside the screen,
   * so that gesture points are never placed off-screen.
   */
  static GestureArea visiblePart(Rectangle container, Dimension screen) {
    int left = max(container.getX(), 0);
    int top = max(container.getY(), 0);
    int right = min(container.getX() + container.getWidth(), screen.getWidth());
    int bottom = min(container.getY() + container.getHeight(), screen.getHeight());
    Dimension size = new Dimension(max(right - left, 0), max(bottom - top, 0));
    return new GestureArea(new Rectangle(new Point(left, top), size));
  }

  AppiumScrollCoordinates scrollCoordinates(ScrollDirection direction, float topPointHeightPercent, float bottomPointHeightPercent) {
    int x = area.getX() + area.getWidth() / 2;
    int topY = area.getY() + (int) (area.getHeight() * topPointHeightPercent);
    int bottomY = area.getY() + (int) (area.getHeight() * bottomPointHeightPercent);
    return direction == ScrollDirection.UP ?
      new AppiumScrollCoordinates(x, topY, x, bottomY) :
      new AppiumScrollCoordinates(x, bottomY, x, topY);
  }

  AppiumScrollCoordinates swipeCoordinates(AppiumSwipeDirection direction) {
    int y = area.getY() + area.getHeight() / 2;
    int leftX = area.getX() + (int) (area.getWidth() * SWIPE_LEFT_POINT_WIDTH_PERCENT);
    int rightX = area.getX() + (int) (area.getWidth() * SWIPE_RIGHT_POINT_WIDTH_PERCENT);
    return direction == RIGHT ?
      new AppiumScrollCoordinates(rightX, y, leftX, y) :
      new AppiumScrollCoordinates(leftX, y, rightX, y);
  }

  Rectangle rectangle() {
    return area;
  }
}
