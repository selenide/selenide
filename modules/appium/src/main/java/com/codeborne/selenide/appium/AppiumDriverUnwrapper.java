package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.SearchContext;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.instanceOf;

@ParametersAreNonnullByDefault
public class AppiumDriverUnwrapper {
  @CheckReturnValue
  public static boolean isMobile(Driver driver) {
    return instanceOf(driver, AppiumDriver.class);
  }

  @CheckReturnValue
  public static boolean isMobile(SearchContext driver) {
    return instanceOf(driver, AppiumDriver.class);
  }

  @CheckReturnValue
  public static boolean isAndroid(SearchContext driver) {
    return instanceOf(driver, AndroidDriver.class);
  }

  @CheckReturnValue
  public static boolean isAndroid(Driver driver) {
    return instanceOf(driver, AndroidDriver.class);
  }

  @CheckReturnValue
  public static boolean isIos(SearchContext driver) {
    return instanceOf(driver, IOSDriver.class);
  }

  @CheckReturnValue
  public static boolean isIos(Driver driver) {
    return instanceOf(driver, IOSDriver.class);
  }
}
