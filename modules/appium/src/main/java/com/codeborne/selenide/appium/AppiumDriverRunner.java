package com.codeborne.selenide.appium;

import com.codeborne.selenide.WebDriverRunner;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

import static com.codeborne.selenide.appium.SelenideAppium.isAndroidDriver;
import static com.codeborne.selenide.appium.SelenideAppium.isIosDriver;
import static com.codeborne.selenide.appium.WebdriverUnwrapper.cast;

public class AppiumDriverRunner {

  private AppiumDriverRunner() {
  }

  public static AndroidDriver getAndroidDriver() {
    return cast(WebDriverRunner.getWebDriver(), AndroidDriver.class)
      .orElseThrow(() -> new ClassCastException("WebDriver cannot be casted to AndroidDriver"));
  }

  public static IOSDriver getIosDriver() {
    return cast(WebDriverRunner.getWebDriver(), IOSDriver.class)
      .orElseThrow(() -> new ClassCastException("WebDriver cannot be casted to IosDriver"));
  }

  public static <T extends AppiumDriver> T getMobileDriver() {
    if (isAndroidDriver() || isIosDriver()) {
      throw new ClassCastException("WebDriver is not instance of Android or Ios Driver");
    }
    return isAndroidDriver() ? (T) getAndroidDriver() : (T) getIosDriver();
  }
}
