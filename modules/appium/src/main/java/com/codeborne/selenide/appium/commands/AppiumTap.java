package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.appium.AppiumClickOptions.tap;

@ParametersAreNonnullByDefault
public class AppiumTap extends AppiumClick {

  @Nonnull
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    if (args == null || args.length == 0) {
      args = new Object[] {tap()};
    }
    return super.execute(proxy, locator, args);
  }
}
