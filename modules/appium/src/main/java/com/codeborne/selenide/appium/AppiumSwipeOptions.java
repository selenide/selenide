package com.codeborne.selenide.appium;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.appium.AppiumSwipeDirection.LEFT;
import static com.codeborne.selenide.appium.AppiumSwipeDirection.RIGHT;

@ParametersAreNonnullByDefault
public class AppiumSwipeOptions {
  private final AppiumSwipeDirection appiumSwipeDirection;
  private final int maxSwipeCounts;
  private static final int DEFAULT_MAX_SWIPE_COUNTS = 30;

  private AppiumSwipeOptions(AppiumSwipeDirection appiumSwipeDirection, int maxSwipeCounts) {
    this.appiumSwipeDirection = appiumSwipeDirection;
    this.maxSwipeCounts = maxSwipeCounts;
  }

  @Nonnull
  @CheckReturnValue
  public static AppiumSwipeOptions with(AppiumSwipeDirection appiumSwipeDirection, int maxSwipeCounts) {
    return new AppiumSwipeOptions(appiumSwipeDirection, maxSwipeCounts);
  }

  @Nonnull
  @CheckReturnValue
  public static AppiumSwipeOptions right(int maxSwipeCounts) {
    return new AppiumSwipeOptions(RIGHT, maxSwipeCounts);
  }

  @Nonnull
  @CheckReturnValue
  public static AppiumSwipeOptions right() {
    return new AppiumSwipeOptions(RIGHT, DEFAULT_MAX_SWIPE_COUNTS);
  }

  @Nonnull
  @CheckReturnValue
  public static AppiumSwipeOptions left(int maxSwipeCounts) {
    return new AppiumSwipeOptions(LEFT, maxSwipeCounts);
  }

  @Nonnull
  @CheckReturnValue
  public static AppiumSwipeOptions left() {
    return new AppiumSwipeOptions(LEFT, DEFAULT_MAX_SWIPE_COUNTS);
  }

  @CheckReturnValue
  public int getMaxSwipeCounts() {
    return this.maxSwipeCounts;
  }

  @Nonnull
  @CheckReturnValue
  public AppiumSwipeDirection getAppiumSwipeDirection() {
    return this.appiumSwipeDirection;
  }
}
