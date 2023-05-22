package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.SetValue;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.appium.WebdriverUnwrapper.isMobile;
import static com.google.common.base.MoreObjects.firstNonNull;
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class AppiumSetValue extends SetValue {
  AppiumSetValue() {
    super(new AppiumClear());
  }

  @Nonnull
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    if (isMobile(locator.driver())) {
      WebElement element = locator.findAndAssertElementIsInteractable();
      CharSequence text = firstNonNull((CharSequence) requireNonNull(args)[0], "");
      element.clear();
      element.sendKeys(text);
      return proxy;
    }
    else {
      return super.execute(proxy, locator, args);
    }
  }
}
