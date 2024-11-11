package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.impl.WebElementSource;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import java.util.Optional;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.cast;

public class HideKeyboard extends FluentCommand {
  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    Optional<AndroidDriver> androidDriver = cast(locator.driver().getWebDriver(), AndroidDriver.class);
    Optional<IOSDriver> iosDriver = cast(locator.driver().getWebDriver(), IOSDriver.class);

    if (androidDriver.isPresent()) {
      hideKeyBoardForAndroid(androidDriver.get());
    }
    else if (iosDriver.isPresent()) {
      hideKeyBoardForIos(iosDriver.get(), locator.getWebElement());
    }
    else {
      throw new UnsupportedOperationException("Cannot hide keyboard for webdriver " + locator.driver().getWebDriver());
    }
  }

  private void hideKeyBoardForAndroid(AndroidDriver driver) {
    if (driver.isKeyboardShown()) {
      driver.hideKeyboard();
    }
  }

  private void hideKeyBoardForIos(IOSDriver driver, WebElement element) {
    if (driver.isKeyboardShown()) {
      element.sendKeys("\n");
    }
  }
}
