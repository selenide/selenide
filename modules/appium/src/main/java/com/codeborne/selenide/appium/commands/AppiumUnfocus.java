package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.Unfocus;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;

@ParametersAreNonnullByDefault
public class AppiumUnfocus extends Unfocus {

  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    if (!isMobile(locator.driver())) {
      return super.execute(proxy, locator, args);
    }
    throw new UnsupportedOperationException("Unfocus is not supported in mobile");
  }
}
