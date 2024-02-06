package com.codeborne.selenide.appium;

import com.codeborne.selenide.WebDriverRunner;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.slf4j.helpers.CheckReturnValue;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isAndroid;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isIos;
import static com.codeborne.selenide.impl.WebdriverUnwrapper.cast;

public class AppiumDriverRunner {

  /**
   * Get the underlying instance of AndroidDriver
   * This can be used for any operations directly with AndroidDriver.
   */
  @Nonnull
  @CheckReturnValue
  public static AndroidDriver getAndroidDriver() {
    return cast(WebDriverRunner.getWebDriver(), AndroidDriver.class)
      .orElseThrow(() -> new ClassCastException("WebDriver cannot be casted to AndroidDriver"));
  }

  /**
   * Get the underlying instance of IOSDriver
   * This can be used for any operations directly with IOSDriver.
   */
  @Nonnull
  @CheckReturnValue
  public static IOSDriver getIosDriver() {
    return cast(WebDriverRunner.getWebDriver(), IOSDriver.class)
      .orElseThrow(() -> new ClassCastException("WebDriver cannot be casted to IosDriver"));
  }

  /**
   * Get the underlying instance of AppiumDriver
   * This can be used for any operations directly with AppiumDriver.
   */
  @Nonnull
  @CheckReturnValue
  @SuppressWarnings("unchecked")
  public static <T extends AppiumDriver> T getMobileDriver() {
    if (isAndroidDriver() || isIosDriver()) {
      return isAndroidDriver() ? (T) getAndroidDriver() : (T) getIosDriver();
    }
    throw new ClassCastException("WebDriver is not instance of AndroidDriver or IOSDriver: " + WebDriverRunner.getWebDriver());
  }

  /**
   * Checks that current driver is AndroidDriver
   * @return false if session is not created
   */
  @CheckReturnValue
  public static boolean isAndroidDriver() {
    return isAndroid(WebDriverRunner.getWebDriver());
  }

  /**
   * Checks that current driver is IOSDriver
   * @return false if session is not created
   */
  @CheckReturnValue
  public static boolean isIosDriver() {
    return isIos(WebDriverRunner.getWebDriver());
  }
}
