package com.codeborne.selenide.appium;

import com.codeborne.selenide.ClickMethod;
import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.appium.commands.AppiumClickMethod;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;

import static java.time.Duration.ZERO;
import static java.time.Duration.ofSeconds;

@ParametersAreNonnullByDefault
public class AppiumClickOptions extends ClickOptions {

  private final AppiumClickMethod appiumClickMethod;
  private final Duration longPressHoldDuration;

  private static final Duration DEFAULT_LONG_PRESS_DURATION = ofSeconds(3);

  private AppiumClickOptions(AppiumClickMethod appiumClickMethod, int offsetX, int offsetY, Duration longPressHoldDuration) {
    super(ClickMethod.DEFAULT, offsetX, offsetY, null, true);
    this.appiumClickMethod = appiumClickMethod;
    this.longPressHoldDuration = longPressHoldDuration;
  }

  @Nonnull
  @CheckReturnValue
  public static AppiumClickOptions tap() {
    return new AppiumClickOptions(AppiumClickMethod.TAP, 0, 0, ZERO);
  }

  @Nonnull
  @CheckReturnValue
  public static AppiumClickOptions tapWithOffset(int xOffset, int yOffset) {
    return new AppiumClickOptions(AppiumClickMethod.TAP_WITH_OFFSET, xOffset, yOffset, ZERO);
  }

  @Nonnull
  @CheckReturnValue
  public static AppiumClickOptions doubleTap() {
    return new AppiumClickOptions(AppiumClickMethod.DOUBLE_TAP, 0, 0, ZERO);
  }

  @Nonnull
  @CheckReturnValue
  public static AppiumClickOptions longPress() {
    return new AppiumClickOptions(AppiumClickMethod.LONG_PRESS, 0, 0, DEFAULT_LONG_PRESS_DURATION);
  }

  @Nonnull
  @CheckReturnValue
  public static AppiumClickOptions longPressFor(Duration longPressHoldDuration) {
    return new AppiumClickOptions(AppiumClickMethod.LONG_PRESS, 0, 0, longPressHoldDuration);
  }

  @Nonnull
  @CheckReturnValue
  public AppiumClickMethod appiumClickMethod() {
    return appiumClickMethod;
  }

  @Nonnull
  @CheckReturnValue
  public Duration longPressHoldDuration() {
    return this.longPressHoldDuration;
  }
}
