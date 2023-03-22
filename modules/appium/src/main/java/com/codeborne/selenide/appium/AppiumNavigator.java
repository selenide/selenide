package com.codeborne.selenide.appium;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

import static com.codeborne.selenide.appium.WebdriverUnwrapper.instanceOf;

@ParametersAreNonnullByDefault
public class AppiumNavigator {

  public void launchApp(Supplier<Runnable> driverSupplier) {
    Configuration.browserSize = null;
    Configuration.pageLoadTimeout = 0;
    SelenideLogger.run("launch app", "", driverSupplier.get());
  }

  public void terminateApp(AppiumDriver driver, String appId) {
    SelenideLogger.run("terminate app", appId, () -> {
      if (instanceOf(driver, AndroidDriver.class)) {
        AppiumDriverRunner.getAndroidDriver().terminateApp(appId);
      } else if (instanceOf(driver, IOSDriver.class)) {
        AppiumDriverRunner.getIosDriver().terminateApp(appId);
      } else {
        throw new IllegalArgumentException("Cannot terminate application for unknown driver " + driver.getClass());
      }
    });
  }

}
