package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.commands.SetValue;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;
import static com.google.common.base.MoreObjects.firstNonNull;
import static java.util.Objects.requireNonNull;

public class AppiumSetValue extends SetValue {
  AppiumSetValue() {
    super(new AppiumClear());
  }

  @Override
  protected void execute(WebElementSource locator, Object[] args) {
    if (isMobile(locator.driver())) {
      WebElement element = locator.findAndAssertElementIsInteractable();
      CharSequence text = firstNonNull((CharSequence) requireNonNull(args)[0], "");
      element.clear();
      element.sendKeys(text);
    }
    else {
      super.execute(locator, args);
    }
  }
}
