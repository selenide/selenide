package com.codeborne.selenide.appium;

import com.codeborne.selenide.SelenideElement;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SelenideAppiumElement extends SelenideElement {
  @Nonnull
  @CanIgnoreReturnValue
  SelenideAppiumElement hideKeyboard();

  @Override
  @Nonnull
  @CanIgnoreReturnValue
  SelenideAppiumElement scrollTo();

  @Nonnull
  @CanIgnoreReturnValue
  SelenideAppiumElement scroll(AppiumScrollOptions appiumScrollOptions);

  @Nonnull
  @CanIgnoreReturnValue
  SelenideAppiumElement swipeTo();

  @Nonnull
  @CanIgnoreReturnValue
  SelenideAppiumElement swipe(AppiumSwipeOptions appiumSwipeOptions);

  @Nonnull
  @CanIgnoreReturnValue
  SelenideAppiumElement tap();

  @Nonnull
  @CanIgnoreReturnValue
  SelenideAppiumElement tap(AppiumClickOptions appiumClickOptions);

  @Nonnull
  @CanIgnoreReturnValue
  SelenideAppiumElement doubleTap();
}
