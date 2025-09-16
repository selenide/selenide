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
import static org.openqa.selenium.remote.CapabilityType.BROWSER_NAME;
import static org.openqa.selenium.remote.CapabilityType.PLATFORM_NAME;

public class AppiumDriverUnwrapper {
  public static boolean isMobile(Driver driver) {
    return isMobile(driver.getWebDriver());
  }

  public static boolean isMobile(SearchContext driver) {
    if (hasCapability(driver, APPIUM_PREFIX + AUTOMATION_NAME_OPTION)) return true;
    if (isWeb(driver)) return false;
    return instanceOf(driver, AppiumDriver.class);
  }

  public static boolean isAndroid(SearchContext driver) {
    if (isPlatform(driver, Platform.ANDROID)) return true;
    if (isPlatform(driver, Platform.IOS)) return false;
    if (isWeb(driver)) return false;
    return instanceOf(driver, AndroidDriver.class);
  }

  public static boolean isAndroid(Driver driver) {
    return isAndroid(driver.getWebDriver());
  }

  public static boolean isIos(SearchContext driver) {
    if (isPlatform(driver, Platform.IOS)) return true;
    if (isPlatform(driver, Platform.ANDROID)) return false;
    if (isWeb(driver)) return false;
    return instanceOf(driver, IOSDriver.class);
  }

  public static boolean isIos(Driver driver) {
    return isIos(driver.getWebDriver());
  }

  @SuppressWarnings("SameParameterValue")
  private static boolean hasCapability(SearchContext driver, String capabilityName) {
    if (driver instanceof HasCapabilities hasCapabilities) {
      Capabilities caps = hasCapabilities.getCapabilities();
      Object value = caps.getCapability(capabilityName);
      return value != null;
    }
    else {
      return false;
    }
  }

  private static boolean isWeb(SearchContext driver) {
    return hasCapability(driver, BROWSER_NAME);
  }

  private static boolean isPlatform(SearchContext driver, Platform platform) {
    if (driver instanceof HasCapabilities hasCapabilities) {
      Capabilities caps = hasCapabilities.getCapabilities();
      Platform platformName = (Platform) caps.getCapability(PLATFORM_NAME);
      return platform.equals(platformName);
    }
    else {
      return false;
    }
  }
}
