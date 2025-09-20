package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.impl.WebElementSource;
import io.appium.java_client.HasOnScreenKeyboard;
import io.appium.java_client.HidesKeyboard;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isAndroid;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isIos;

public class HideKeyboard extends FluentCommand {
  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    WebDriver webDriver = locator.driver().getWebDriver();

    if (isAndroid(webDriver)) {
      hideKeyBoardForAndroid((HasOnScreenKeyboard & HidesKeyboard) webDriver);
    }
    else if (isIos(webDriver)) {
      hideKeyBoardForIos((HasOnScreenKeyboard) webDriver, locator.getWebElement());
    }
    else {
      throw new UnsupportedOperationException("Cannot hide keyboard for webdriver " + webDriver);
    }
  }

  private <T extends HasOnScreenKeyboard & HidesKeyboard> void hideKeyBoardForAndroid(T driver) {
    if (driver.isKeyboardShown()) {
      driver.hideKeyboard();
    }
  }

  private void hideKeyBoardForIos(HasOnScreenKeyboard driver, WebElement element) {
    if (driver.isKeyboardShown()) {
      element.sendKeys("\n");
    }
  }
}
