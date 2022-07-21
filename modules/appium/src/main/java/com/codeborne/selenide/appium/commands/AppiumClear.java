package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.Clear;
import com.codeborne.selenide.impl.WebElementSource;
import io.appium.java_client.AppiumDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.appium.WebdriverUnwrapper.instanceOf;

@ParametersAreNonnullByDefault
public class AppiumClear extends Clear {
  @Nonnull
  @CheckReturnValue
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    if (instanceOf(locator.driver(), AppiumDriver.class)) {
      locator.findAndAssertElementIsInteractable().clear();
      return proxy;
    }
    else {
      return super.execute(proxy, locator, args);
    }
  }
}
