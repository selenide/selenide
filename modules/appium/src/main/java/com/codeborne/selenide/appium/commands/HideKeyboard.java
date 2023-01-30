package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

import javax.annotation.Nullable;
import java.util.Optional;

import static com.codeborne.selenide.appium.WebdriverUnwrapper.cast;

public class HideKeyboard implements Command<Object> {

  @Override
  @Nullable
  public Object execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    Optional<AndroidDriver> androidDriver = cast(locator.driver().getWebDriver(), AndroidDriver.class);
    Optional<IOSDriver> iosDriver = cast(locator.driver().getWebDriver(), IOSDriver.class);

    if (androidDriver.isPresent()) {
      hideKeyBoardForAndroid(androidDriver.get());
    }
    else if (iosDriver.isPresent()) {
      hideKeyBoardForIos(iosDriver.get(), proxy);
    }
    else {
      throw new UnsupportedOperationException("Cannot hide keyboard for webdriver " + locator.driver().getWebDriver());
    }
    return proxy;
  }

  private void hideKeyBoardForAndroid(AndroidDriver driver) {
    if (driver.isKeyboardShown()) {
      driver.hideKeyboard();
    }
  }

  private void hideKeyBoardForIos(IOSDriver driver, SelenideElement element) {
    if (driver.isKeyboardShown()) {
      element.sendKeys("\n");
    }
  }
}
