package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.commands.Clear;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;

public class AppiumClear extends Clear {
  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    if (isMobile(locator.driver())) {
      locator.findAndAssertElementIsVisible().clear();
    }
    else {
      super.execute(locator, args);
    }
  }
}
