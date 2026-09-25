package com.codeborne.selenide.appium;

import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.appium.AppiumSwipeDirection.LEFT;
import static com.codeborne.selenide.appium.AppiumSwipeDirection.RIGHT;

public class AppiumSwipeOptions {
  private final AppiumSwipeDirection appiumSwipeDirection;
  private final int maxSwipeCounts;
  private final @Nullable WebElement container;
  private static final int DEFAULT_MAX_SWIPE_COUNTS = 30;

  private AppiumSwipeOptions(AppiumSwipeDirection appiumSwipeDirection, int maxSwipeCounts) {
    this(appiumSwipeDirection, maxSwipeCounts, null);
  }

  private AppiumSwipeOptions(AppiumSwipeDirection appiumSwipeDirection, int maxSwipeCounts, @Nullable WebElement container) {
    this.appiumSwipeDirection = appiumSwipeDirection;
    this.maxSwipeCounts = maxSwipeCounts;
    this.container = container;
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

  /**
   * Perform swipe gestures inside the given element (e.g. a carousel) instead of the whole screen.
   */
  public AppiumSwipeOptions inside(WebElement container) {
    return new AppiumSwipeOptions(appiumSwipeDirection, maxSwipeCounts, container);
  }

  public int getMaxSwipeCounts() {
    return this.maxSwipeCounts;
  }

  public AppiumSwipeDirection getAppiumSwipeDirection() {
    return this.appiumSwipeDirection;
  }

  public @Nullable WebElement getContainer() {
    return container;
  }

  @Override
  public String toString() {
    return String.format("%s, max swipes: %s%s", appiumSwipeDirection, maxSwipeCounts,
      container == null ? "" : ", inside: " + container);
  }
}
