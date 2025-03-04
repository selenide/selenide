package com.codeborne.selenide.appium;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.appmanagement.BaseTerminateApplicationOptions;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.cast;

public class AppiumNavigator {

  public void launchApp(Supplier<Runnable> driverSupplier) {
    Configuration.browserSize = null;
    Configuration.pageLoadTimeout = -1;
    SelenideLogger.run("launch app", "", driverSupplier.get());
  }

  public void activateApp(AppiumDriver driver, String appId) {
    SelenideLogger.run(
      "activate app",
      appId,
      () -> cast(driver, InteractsWithApps.class)
        .map(mobileDriver -> {
          mobileDriver.activateApp(appId);
          return true;
        })
        .orElseThrow(() -> new UnsupportedOperationException("Driver does not support app activation: " + driver.getClass()))
    );
  }

  public void terminateApp(AppiumDriver driver, String appId, @Nullable Duration timeout) {
    SelenideLogger.run(
      "terminate app",
      appId,
      () -> cast(driver, InteractsWithApps.class)
        .map(mobileDriver -> {
          if (Objects.nonNull(timeout)) {
            BaseTerminateApplicationOptions options = (BaseTerminateApplicationOptions) Map.of("timeout", timeout.toMillis());
            return mobileDriver.terminateApp(appId, options);
          } else {
            return mobileDriver.terminateApp(appId);
          }
        })
        .orElseThrow(() -> new UnsupportedOperationException("Driver does not support app termination: " + driver.getClass()))
    );
  }
}
