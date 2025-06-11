package com.codeborne.selenide.appium;

import java.time.Duration;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
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

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldNotBe(WebElementCondition condition, Duration timeout);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldNotBe(WebElementCondition... condition);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldNotHave(WebElementCondition condition, Duration timeout);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldNotHave(WebElementCondition... condition);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldNot(WebElementCondition condition, Duration timeout);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldNot(WebElementCondition... condition);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldBe(WebElementCondition condition, Duration timeout);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldBe(WebElementCondition... condition);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldHave(WebElementCondition condition, Duration timeout);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement shouldHave(WebElementCondition... condition);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement should(WebElementCondition condition, Duration timeout);

  @Override
  @CanIgnoreReturnValue
  SelenideAppiumElement should(WebElementCondition... condition);
}
