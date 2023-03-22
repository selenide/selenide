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
}
