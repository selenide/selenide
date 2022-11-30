package com.codeborne.selenide.appium;

import static com.codeborne.selenide.appium.WebdriverUnwrapper.instanceOf;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import java.util.HashMap;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AppiumNavigator {

  public void launchApp(Supplier<Runnable> driverSupplier) {
    Configuration.browserSize = null;
    Configuration.pageLoadTimeout = 0;
    SelenideLogger.run("launch app", "", driverSupplier.get());
  }

  public void terminateApp(AppiumDriver driver, String appId) {
    SelenideLogger.run("terminate app", appId, () -> {
      HashMap<String, String> params = new HashMap<>();
      if (instanceOf(driver, AndroidDriver.class)) {
        params.put("appId", appId);
      } else if (instanceOf(driver, IOSDriver.class)) {
        params.put("bundleId", appId);
      } else {
        throw new IllegalArgumentException("Cannot terminate application for unknown driver " + driver.getClass());
      }
      driver.executeScript("mobile:terminateApp", params);
    });
  }

}
