package com.codeborne.selenide.appium;

import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

public class AppiumScrollOptions {

  private final ScrollDirection scrollDirection;
  private final int maxSwipeCount;
  private final float topPointHeightPercent;
  private final float bottomPointHeightPercent;
  private final @Nullable WebElement container;

  private static final int DEFAULT_MAX_SWIPE_COUNTS = 30;
  private static final float DEFAULT_TOP_POINT_HEIGHT_PERCENT = 0.25f;
  private static final float DEFAULT_BOTTOM_POINT_HEIGHT_PERCENT = 0.5f;

  private AppiumScrollOptions(ScrollDirection scrollDirection, int maxSwipeCount) {
    this(scrollDirection, maxSwipeCount, DEFAULT_TOP_POINT_HEIGHT_PERCENT, DEFAULT_BOTTOM_POINT_HEIGHT_PERCENT);
  }

  private AppiumScrollOptions(ScrollDirection scrollDirection, int maxSwipeCount,
                              float topPointHeightPercent, float bottomPointHeightPercent) {
    this(scrollDirection, maxSwipeCount, topPointHeightPercent, bottomPointHeightPercent, null);
  }

  private AppiumScrollOptions(ScrollDirection scrollDirection, int maxSwipeCount,
                              float topPointHeightPercent, float bottomPointHeightPercent, @Nullable WebElement container) {
    checkCoordinateValues(topPointHeightPercent, bottomPointHeightPercent);
    this.scrollDirection = scrollDirection;
    this.maxSwipeCount = maxSwipeCount;
    this.topPointHeightPercent = validatePercentage("Top point height percentage", topPointHeightPercent);
    this.bottomPointHeightPercent = validatePercentage("Bottom point height percentage", bottomPointHeightPercent);
    this.container = container;
  }

  private static float validatePercentage(String name, float value) {
    if (value <= 0 || value >= 1) {
      throw new IllegalArgumentException(name + " should be greater than 0 and less than 1. Example: (0.2, 0.5)");
    }
    return value;
  }

  private void checkCoordinateValues(float topPointHeightPercent, float bottomPointHeightPercent) {
    if (topPointHeightPercent > bottomPointHeightPercent) {
      throw new IllegalArgumentException("Top point percentage should be less than bottom point percentage. Example: (0.1, 0.5)");
    }
  }

  public static AppiumScrollOptions with(ScrollDirection scrollDirection, int maxSwipeCount) {
    return new AppiumScrollOptions(scrollDirection, maxSwipeCount);
  }

  public static AppiumScrollOptions with(ScrollDirection scrollDirection, float topPointHeightPercent, float bottomPointHeightPercent) {
    return new AppiumScrollOptions(scrollDirection, DEFAULT_MAX_SWIPE_COUNTS, topPointHeightPercent, bottomPointHeightPercent);
  }

  public static AppiumScrollOptions down() {
    return new AppiumScrollOptions(ScrollDirection.DOWN, DEFAULT_MAX_SWIPE_COUNTS);
  }

  public static AppiumScrollOptions down(int maxSwipeCount) {
    return new AppiumScrollOptions(ScrollDirection.DOWN, maxSwipeCount);
  }

  public static AppiumScrollOptions up() {
    return new AppiumScrollOptions(ScrollDirection.UP, DEFAULT_MAX_SWIPE_COUNTS);
  }

  public static AppiumScrollOptions up(int maxSwipeCount) {
    return new AppiumScrollOptions(ScrollDirection.UP, maxSwipeCount);
  }

  public static AppiumScrollOptions up(float topPointHeightPercent, float bottomPointHeightPercent) {
    return new AppiumScrollOptions(ScrollDirection.UP, DEFAULT_MAX_SWIPE_COUNTS, topPointHeightPercent, bottomPointHeightPercent);
  }

  public static AppiumScrollOptions down(float topPointHeightPercent, float bottomPointHeightPercent) {
    return new AppiumScrollOptions(ScrollDirection.DOWN, DEFAULT_MAX_SWIPE_COUNTS, topPointHeightPercent, bottomPointHeightPercent);
  }

  /**
   * Perform scroll gestures inside the given element (e.g. a scrollable list) instead of the whole screen.
   * Top/bottom point percentages are then calculated relative to the element's height.
   */
  public AppiumScrollOptions inside(WebElement container) {
    return new AppiumScrollOptions(scrollDirection, maxSwipeCount, topPointHeightPercent, bottomPointHeightPercent, container);
  }

  /**
   * @deprecated Use {@link #getMaxSwipeCount()} instead
   */
  @Deprecated
  public int getMaxSwipeCounts() {
    return maxSwipeCount;
  }

  public int getMaxSwipeCount() {
    return maxSwipeCount;
  }

  public ScrollDirection getScrollDirection() {
    return scrollDirection;
  }

  public float getTopPointHeightPercent() {
    return topPointHeightPercent;
  }

  public float getBottomPointHeightPercent() {
    return bottomPointHeightPercent;
  }

  public @Nullable WebElement getContainer() {
    return container;
  }

  @Override
  public String toString() {
    return String.format("%s, max swipes: %s, top height: %s, bottom height: %s%s",
      scrollDirection, maxSwipeCount, topPointHeightPercent, bottomPointHeightPercent,
      container == null ? "" : ", inside: " + container);
  }
}
