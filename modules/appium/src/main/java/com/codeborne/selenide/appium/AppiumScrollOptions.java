package com.codeborne.selenide.appium;

public class AppiumScrollOptions {

  private final ScrollDirection scrollDirection;
  private final int maxSwipeCounts;
  private static final int DEFAULT_MAX_SWIPE_COUNTS = 30;
  private final float topPointHeightPercent;
  private static final float DEFAULT_TOP_POINT_HEIGHT_PERCENT = 0.25f;
  private final float bottomPointHeightPercent;
  private static final float DEFAULT_BOTTOM_POINT_HEIGHT_PERCENT = 0.5f;

  private AppiumScrollOptions(ScrollDirection scrollDirection, int maxSwipeCounts) {
    this(scrollDirection, maxSwipeCounts, DEFAULT_TOP_POINT_HEIGHT_PERCENT, DEFAULT_BOTTOM_POINT_HEIGHT_PERCENT);
  }

  private AppiumScrollOptions(ScrollDirection scrollDirection, int maxSwipeCounts,
                              float topPointHeightPercent, float bottomPointHeightPercent) {
    this.scrollDirection = scrollDirection;
    this.maxSwipeCounts = maxSwipeCounts;
    this.topPointHeightPercent = topPointHeightPercent;
    this.bottomPointHeightPercent = bottomPointHeightPercent;
  }

  public static AppiumScrollOptions with(ScrollDirection scrollDirection, int maxSwipeCounts) {
    return new AppiumScrollOptions(scrollDirection, maxSwipeCounts);
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

  public static AppiumScrollOptions up(float top, float bottom) {
    return new AppiumScrollOptions(ScrollDirection.UP, DEFAULT_MAX_SWIPE_COUNTS, top, bottom);
  }

  public static AppiumScrollOptions down(float top, float bottom) {
    return new AppiumScrollOptions(ScrollDirection.DOWN, DEFAULT_MAX_SWIPE_COUNTS, top, bottom);
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
