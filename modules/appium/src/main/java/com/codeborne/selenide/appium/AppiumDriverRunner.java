package com.codeborne.selenide.appium;

import com.codeborne.selenide.WebDriverRunner;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HidesKeyboard;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.SupportsRotation;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isAndroid;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isIos;
import static com.codeborne.selenide.impl.WebdriverUnwrapper.cast;

public class AppiumDriverRunner {

  /**
   * Get the underlying instance of AndroidDriver
   * This can be used for any operations directly with AndroidDriver.
   *
   * @deprecated instead of retrieving {@link AndroidDriver},
   * cast your {@link WebDriver} to the needed interface, e.g. {@link SupportsRotation} or {@link HidesKeyboard}.
   */
  @Deprecated(forRemoval = true)
  @SuppressWarnings("removal")
  public static AndroidDriver getAndroidDriver() {
    return cast(WebDriverRunner.getWebDriver(), AndroidDriver.class)
      .orElseThrow(() -> new ClassCastException("WebDriver cannot be casted to AndroidDriver"));
  }

  /**
   * Get the underlying instance of IOSDriver
   * This can be used for any operations directly with IOSDriver.
   *
   * @deprecated instead of retrieving {@link IOSDriver},
   * cast your {@link WebDriver} to the needed interface, e.g. {@link SupportsRotation} or {@link HidesKeyboard}.
   */
  @Deprecated(forRemoval = true)
  @SuppressWarnings("removal")
  public static IOSDriver getIosDriver() {
    return cast(WebDriverRunner.getWebDriver(), IOSDriver.class)
      .orElseThrow(() -> new ClassCastException("WebDriver cannot be casted to IosDriver"));
  }

  /**
   * Get the underlying instance of AppiumDriver
   * This can be used for any operations directly with AppiumDriver.
   *
   * @deprecated instead of retrieving {@link AndroidDriver},
   * cast your {@link WebDriver} to the needed interface, e.g. {@link SupportsRotation} or {@link HidesKeyboard}.
   */
  @SuppressWarnings("unchecked")
  @Deprecated(forRemoval = true)
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
  public static boolean isAndroidDriver() {
    return isAndroid(WebDriverRunner.getWebDriver());
  }

  /**
   * Checks that current driver is IOSDriver
   * @return false if session is not created
   */
  public static boolean isIosDriver() {
    return isIos(WebDriverRunner.getWebDriver());
  }
}
