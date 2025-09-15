package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.SearchContext;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.instanceOf;
import static io.appium.java_client.internal.CapabilityHelpers.APPIUM_PREFIX;
import static io.appium.java_client.remote.options.SupportsAutomationNameOption.AUTOMATION_NAME_OPTION;
import static org.openqa.selenium.remote.CapabilityType.PLATFORM_NAME;

public class AppiumDriverUnwrapper {
  public static boolean isMobile(Driver driver) {
    return isMobile(driver.getWebDriver());
  }

  public static boolean isMobile(SearchContext driver) {
    return hasCapability(driver, APPIUM_PREFIX + AUTOMATION_NAME_OPTION)
           || instanceOf(driver, AppiumDriver.class);
  }

  public static boolean isAndroid(SearchContext driver) {
    return isPlatform(driver, Platform.ANDROID) || instanceOf(driver, AndroidDriver.class);
  }

  public static boolean isAndroid(Driver driver) {
    return isAndroid(driver.getWebDriver());
  }

  public static boolean isIos(SearchContext driver) {
    return isPlatform(driver, Platform.IOS) || instanceOf(driver, IOSDriver.class);
  }

  public static boolean isIos(Driver driver) {
    return isIos(driver.getWebDriver());
  }

  @SuppressWarnings("SameParameterValue")
  private static boolean hasCapability(SearchContext driver, String capabilityName) {
    if (driver instanceof HasCapabilities hasCapabilities) {
      Capabilities caps = hasCapabilities.getCapabilities();
      Object automationName = caps.getCapability(capabilityName);
      return automationName != null;
    }
    else {
      return false;
    }
  }

  private static boolean isPlatform(SearchContext driver, Platform platform) {
    if (driver instanceof HasCapabilities hasCapabilities) {
      Capabilities caps = hasCapabilities.getCapabilities();
      Object automationName = caps.getCapability(PLATFORM_NAME);
      return platform.name().equals(automationName);
    }
    else {
      return false;
    }
  }
}
