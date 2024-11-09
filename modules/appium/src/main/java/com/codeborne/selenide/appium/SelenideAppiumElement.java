package com.codeborne.selenide.appium;

import com.codeborne.selenide.SelenideElement;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

public interface SelenideAppiumElement extends SelenideElement {
  @CanIgnoreReturnValue
  @Override
  SelenideAppiumElement as(String alias);

  /**
   * @see com.codeborne.selenide.appium.commands.HideKeyboard
   */
  @CanIgnoreReturnValue
  SelenideAppiumElement hideKeyboard();

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumScrollTo
   */
  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement scrollTo();

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumScrollTo
   */
  @CanIgnoreReturnValue
  SelenideAppiumElement scroll(AppiumScrollOptions appiumScrollOptions);

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumSwipeTo
   */
  @CanIgnoreReturnValue
  SelenideAppiumElement swipeTo();

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumSwipeTo
   */
  @CanIgnoreReturnValue
  SelenideAppiumElement swipe(AppiumSwipeOptions appiumSwipeOptions);

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumTap
   */
  @CanIgnoreReturnValue
  SelenideAppiumElement tap();

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumTap
   */
  @CanIgnoreReturnValue
  SelenideAppiumElement tap(AppiumClickOptions appiumClickOptions);

  /**
   * @see com.codeborne.selenide.appium.commands.AppiumDoubleTap
   */
  @CanIgnoreReturnValue
  SelenideAppiumElement doubleTap();
}
