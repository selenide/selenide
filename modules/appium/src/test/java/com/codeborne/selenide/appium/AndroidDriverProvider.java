package com.codeborne.selenide.appium;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Capabilities;
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
  public WebDriver createDriver(@Nonnull Capabilities capabilities) {
    DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
    desiredCapabilities.merge(capabilities);
    desiredCapabilities.setCapability(PLATFORM_NAME, "Android");
    desiredCapabilities.setCapability(DEVICE_NAME, "Android Emulator");
    desiredCapabilities.setCapability(VERSION, "9.0");
    desiredCapabilities.setCapability(APPLICATION_NAME, "Appium");
    desiredCapabilities.setCapability(APP_PACKAGE, "com.android.calculator2");
    desiredCapabilities.setCapability(APP_ACTIVITY, "com.android.calculator2.Calculator");
    desiredCapabilities.setCapability(NEW_COMMAND_TIMEOUT, 11);
    desiredCapabilities.setCapability(FULL_RESET, false);
    try {
      return new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), desiredCapabilities);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
