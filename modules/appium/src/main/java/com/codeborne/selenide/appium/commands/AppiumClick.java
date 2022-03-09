package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.commands.Click;
import com.codeborne.selenide.impl.WebElementSource;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AppiumClick extends Click {
  @Override
  @Nonnull
  @CheckReturnValue
  protected WebElement findElement(WebElementSource locator) {
    if (locator.driver().getWebDriver() instanceof AppiumDriver) {
      return locator.getWebElement();
    }
    else {
      return super.findElement(locator);
    }
  }
}
