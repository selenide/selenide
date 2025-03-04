package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.commands.Unfocus;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;

public class AppiumUnfocus extends Unfocus {

  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    if (!isMobile(locator.driver())) {
      super.execute(locator, args);
    }
    else {
      throw new UnsupportedOperationException("Unfocus is not supported in mobile");
    }
  }
}
