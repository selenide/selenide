package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.appium.AppiumClickOptions.doubleTap;

@ParametersAreNonnullByDefault
public class AppiumDoubleTap extends AppiumClick {

  @Nonnull
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    return super.execute(proxy, locator, new Object[] {doubleTap()});
  }
}
