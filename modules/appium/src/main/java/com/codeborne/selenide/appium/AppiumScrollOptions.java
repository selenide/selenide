package com.codeborne.selenide.appium;

public class AppiumScrollOptions {

  private final ScrollDirection scrollDirection;
  private final int maxSwipeCounts;
  private static final int DEFAULT_MAX_SWIPE_COUNTS = 30;

  private AppiumScrollOptions(ScrollDirection scrollDirection, int maxSwipeCounts) {
    this.scrollDirection = scrollDirection;
    this.maxSwipeCounts = maxSwipeCounts;
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

  public int getMaxSwipeCounts() {
    return maxSwipeCounts;
  }

  public ScrollDirection getScrollDirection() {
    return scrollDirection;
  }

}
