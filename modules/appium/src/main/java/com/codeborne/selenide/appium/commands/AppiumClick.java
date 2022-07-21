package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.commands.Click;
import com.codeborne.selenide.impl.WebElementSource;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.appium.WebdriverUnwrapper.instanceOf;

@ParametersAreNonnullByDefault
public class AppiumClick extends Click {
  @Override
  @Nonnull
  @CheckReturnValue
  protected WebElement findElement(WebElementSource locator) {
    if (instanceOf(locator.driver(), AppiumDriver.class)) {
      return locator.getWebElement();
    }
    else {
      return super.findElement(locator);
    }
  }
}
