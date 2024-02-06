package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.SearchContext;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.instanceOf;

public class AppiumDriverUnwrapper {
  public static boolean isMobile(Driver driver) {
    return instanceOf(driver, AppiumDriver.class);
  }

  public static boolean isMobile(SearchContext driver) {
    return instanceOf(driver, AppiumDriver.class);
  }

  public static boolean isAndroid(SearchContext driver) {
    return instanceOf(driver, AndroidDriver.class);
  }

  public static boolean isAndroid(Driver driver) {
    return instanceOf(driver, AndroidDriver.class);
  }

  public static boolean isIos(SearchContext driver) {
    return instanceOf(driver, IOSDriver.class);
  }

  public static boolean isIos(Driver driver) {
    return instanceOf(driver, IOSDriver.class);
  }
}
