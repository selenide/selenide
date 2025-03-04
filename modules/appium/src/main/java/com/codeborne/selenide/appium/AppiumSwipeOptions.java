package com.codeborne.selenide.appium;

import static com.codeborne.selenide.appium.AppiumSwipeDirection.LEFT;
import static com.codeborne.selenide.appium.AppiumSwipeDirection.RIGHT;

public class AppiumSwipeOptions {
  private final AppiumSwipeDirection appiumSwipeDirection;
  private final int maxSwipeCounts;
  private static final int DEFAULT_MAX_SWIPE_COUNTS = 30;

  private AppiumSwipeOptions(AppiumSwipeDirection appiumSwipeDirection, int maxSwipeCounts) {
    this.appiumSwipeDirection = appiumSwipeDirection;
    this.maxSwipeCounts = maxSwipeCounts;
  }

  public static AppiumSwipeOptions with(AppiumSwipeDirection appiumSwipeDirection, int maxSwipeCounts) {
    return new AppiumSwipeOptions(appiumSwipeDirection, maxSwipeCounts);
  }

  public static AppiumSwipeOptions right(int maxSwipeCounts) {
    return new AppiumSwipeOptions(RIGHT, maxSwipeCounts);
  }

  public static AppiumSwipeOptions right() {
    return new AppiumSwipeOptions(RIGHT, DEFAULT_MAX_SWIPE_COUNTS);
  }

  public static AppiumSwipeOptions left(int maxSwipeCounts) {
    return new AppiumSwipeOptions(LEFT, maxSwipeCounts);
  }

  public static AppiumSwipeOptions left() {
    return new AppiumSwipeOptions(LEFT, DEFAULT_MAX_SWIPE_COUNTS);
  }

  public int getMaxSwipeCounts() {
    return this.maxSwipeCounts;
  }

  public AppiumSwipeDirection getAppiumSwipeDirection() {
    return this.appiumSwipeDirection;
  }
}
