package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import static com.codeborne.selenide.appium.AppiumClickOptions.doubleTap;

public class AppiumDoubleTap extends AppiumClick {
  @Override
  public void execute(WebElementSource locator, Object @Nullable [] args) {
    super.execute(locator, new Object[] {doubleTap()});
  }
}
