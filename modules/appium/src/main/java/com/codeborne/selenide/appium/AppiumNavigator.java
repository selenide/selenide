package com.codeborne.selenide.appium;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;

import java.util.function.Supplier;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.cast;

public class AppiumNavigator {

  public void launchApp(Supplier<Runnable> driverSupplier) {
    Configuration.browserSize = null;
    Configuration.pageLoadTimeout = -1;
    SelenideLogger.run("launch app", "", driverSupplier.get());
  }

  public void terminateApp(AppiumDriver driver, String appId) {
    SelenideLogger.run("terminate app", appId, () -> {
      cast(driver, InteractsWithApps.class)
        .map(mobileDriver -> mobileDriver.terminateApp(appId))
        .orElseThrow(() -> new UnsupportedOperationException("Driver does not support app termination: " + driver.getClass()));
    });
  }

}
