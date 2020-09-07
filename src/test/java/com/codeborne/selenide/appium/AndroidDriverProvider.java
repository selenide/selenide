package com.codeborne.selenide.appium;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;

import static io.appium.java_client.remote.AndroidMobileCapabilityType.APP_ACTIVITY;
import static io.appium.java_client.remote.AndroidMobileCapabilityType.APP_PACKAGE;
import static io.appium.java_client.remote.MobileCapabilityType.DEVICE_NAME;
import static io.appium.java_client.remote.MobileCapabilityType.FULL_RESET;
import static io.appium.java_client.remote.MobileCapabilityType.NEW_COMMAND_TIMEOUT;
import static io.appium.java_client.remote.MobileCapabilityType.PLATFORM_NAME;
import static io.appium.java_client.remote.MobileCapabilityType.VERSION;
import static org.openqa.selenium.remote.CapabilityType.APPLICATION_NAME;

public class AndroidDriverProvider implements WebDriverProvider {
  @CheckReturnValue
  @Nonnull
  @Override
  public WebDriver createDriver(DesiredCapabilities capabilities) {
    capabilities.setCapability(PLATFORM_NAME, "Android");
    capabilities.setCapability(DEVICE_NAME, "Android Emulator");
    capabilities.setCapability(VERSION, "9.0");
    capabilities.setCapability(APPLICATION_NAME, "Appium");
    capabilities.setCapability(APP_PACKAGE, "com.android.calculator2");
    capabilities.setCapability(APP_ACTIVITY, "com.android.calculator2.Calculator");
    capabilities.setCapability(NEW_COMMAND_TIMEOUT, 11);
    capabilities.setCapability(FULL_RESET, false);
    try {
      return new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
