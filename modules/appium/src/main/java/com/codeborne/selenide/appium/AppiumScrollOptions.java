package com.codeborne.selenide.appium;

public class AppiumScrollOptions {

  private final ScrollDirection scrollDirection;
  private final int maxSwipeCounts;
  private final float topPointHeightPercent;
  private final float bottomPointHeightPercent;

  private static final int DEFAULT_MAX_SWIPE_COUNTS = 30;
  private static final float DEFAULT_TOP_POINT_HEIGHT_PERCENT = 0.25f;
  private static final float DEFAULT_BOTTOM_POINT_HEIGHT_PERCENT = 0.5f;

  private AppiumScrollOptions(ScrollDirection scrollDirection, int maxSwipeCounts) {
    this(scrollDirection, maxSwipeCounts, DEFAULT_TOP_POINT_HEIGHT_PERCENT, DEFAULT_BOTTOM_POINT_HEIGHT_PERCENT);
  }

  private AppiumScrollOptions(ScrollDirection scrollDirection, int maxSwipeCounts,
                              float topPointHeightPercent, float bottomPointHeightPercent) {
    checkCoordinateValues(topPointHeightPercent, bottomPointHeightPercent);
    this.scrollDirection = scrollDirection;
    this.maxSwipeCounts = maxSwipeCounts;
    this.topPointHeightPercent = topPointHeightPercent;
    this.bottomPointHeightPercent = bottomPointHeightPercent;
  }

  private void checkCoordinateValues(float topPointHeightPercent, float bottomPointHeightPercent) {
    if (topPointHeightPercent <= 0 || topPointHeightPercent >= 1 ||
      bottomPointHeightPercent <= 0 || bottomPointHeightPercent >= 1) {
      throw new IllegalArgumentException("Percentage height should be greater than 0 and less than 1. Example: (0.2, 0.5)");
    }

    if (topPointHeightPercent > bottomPointHeightPercent) {
      throw new IllegalArgumentException("Top point percentage should be less than bottom point percentage. Example: (0.1, 0.5)");
    }
  }

  public static AppiumScrollOptions with(ScrollDirection scrollDirection, int maxSwipeCounts) {
    return new AppiumScrollOptions(scrollDirection, maxSwipeCounts);
  }

  public static AppiumScrollOptions with(ScrollDirection scrollDirection, float topPointHeightPercent, float bottomPointHeightPercent) {
    return new AppiumScrollOptions(scrollDirection, DEFAULT_MAX_SWIPE_COUNTS, topPointHeightPercent, bottomPointHeightPercent);
  }

  public static AppiumScrollOptions down() {
    return new AppiumScrollOptions(ScrollDirection.DOWN, DEFAULT_MAX_SWIPE_COUNTS);
  }

  public static AppiumScrollOptions down(int maxSwipeCounts) {
    return new AppiumScrollOptions(ScrollDirection.DOWN, maxSwipeCounts);
  }

  public static AppiumScrollOptions up() {
    return new AppiumScrollOptions(ScrollDirection.UP, DEFAULT_MAX_SWIPE_COUNTS);
  }

  public static AppiumScrollOptions up(int maxSwipeCounts) {
    return new AppiumScrollOptions(ScrollDirection.UP, maxSwipeCounts);
  }

  public static AppiumScrollOptions up(float topPointHeightPercent, float bottomPointHeightPercent) {
    return new AppiumScrollOptions(ScrollDirection.UP, DEFAULT_MAX_SWIPE_COUNTS, topPointHeightPercent, bottomPointHeightPercent);
  }

  public static AppiumScrollOptions down(float topPointHeightPercent, float bottomPointHeightPercent) {
    return new AppiumScrollOptions(ScrollDirection.DOWN, DEFAULT_MAX_SWIPE_COUNTS, topPointHeightPercent, bottomPointHeightPercent);
  }

  public int getMaxSwipeCounts() {
    return maxSwipeCounts;
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
}
