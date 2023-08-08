package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.commands.Type;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import static com.codeborne.selenide.appium.WebdriverUnwrapper.isMobile;

public class AppiumType extends Type {
  public AppiumType() {
    super(new AppiumClear());
  }

  @Override
  @Nonnull
  @CheckReturnValue
  protected WebElement findElement(WebElementSource locator) {
    return isMobile(locator.driver()) ?
      locator.findAndAssertElementIsInteractable() :
      super.findElement(locator);
  }
}
