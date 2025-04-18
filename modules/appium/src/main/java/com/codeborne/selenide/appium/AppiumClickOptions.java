package com.codeborne.selenide.appium;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.appium.commands.AppiumClickMethod;

import java.time.Duration;

import static com.codeborne.selenide.ClickMethod.DEFAULT;
import static java.time.Duration.ZERO;
import static java.time.Duration.ofSeconds;
import static java.util.Collections.emptyList;

public class AppiumClickOptions extends ClickOptions {

  private final AppiumClickMethod appiumClickMethod;
  private final Duration longPressHoldDuration;

  private static final Duration DEFAULT_LONG_PRESS_DURATION = ofSeconds(3);

  private AppiumClickOptions(AppiumClickMethod appiumClickMethod, int offsetX, int offsetY, Duration longPressHoldDuration) {
    super(DEFAULT, offsetX, offsetY, null, true, emptyList());
    this.appiumClickMethod = appiumClickMethod;
    this.longPressHoldDuration = longPressHoldDuration;
  }

  public static AppiumClickOptions tap() {
    return new AppiumClickOptions(AppiumClickMethod.TAP, 0, 0, ZERO);
  }

  public static AppiumClickOptions tapWithOffset(int xOffset, int yOffset) {
    return new AppiumClickOptions(AppiumClickMethod.TAP_WITH_OFFSET, xOffset, yOffset, ZERO);
  }

  public static AppiumClickOptions doubleTap() {
    return new AppiumClickOptions(AppiumClickMethod.DOUBLE_TAP, 0, 0, ZERO);
  }

  public static AppiumClickOptions longPress() {
    return new AppiumClickOptions(AppiumClickMethod.LONG_PRESS, 0, 0, DEFAULT_LONG_PRESS_DURATION);
  }

  public static AppiumClickOptions longPressFor(Duration longPressHoldDuration) {
    return new AppiumClickOptions(AppiumClickMethod.LONG_PRESS, 0, 0, longPressHoldDuration);
  }

  public AppiumClickMethod appiumClickMethod() {
    return appiumClickMethod;
  }

  public Duration longPressHoldDuration() {
    return this.longPressHoldDuration;
  }
}
