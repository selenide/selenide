package com.codeborne.selenide.appium;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.appmanagement.AndroidTerminateApplicationOptions;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.function.Supplier;

public class AppiumNavigator {

  public void launchApp(Supplier<Runnable> driverSupplier) {
    Configuration.browserSize = null;
    Configuration.pageLoadTimeout = -1;
    SelenideLogger.run("launch app", "", driverSupplier.get());
  }

  public void activateApp(WebDriver driver, String appId) {
    SelenideLogger.run(
      "activate app",
      appId,
      () -> ((InteractsWithApps) driver).activateApp(appId)
    );
  }

  public void terminateApp(WebDriver driver, String appId, @Nullable Duration timeout) {
    SelenideLogger.run(
      "terminate app",
      appId,
      () -> {
        InteractsWithApps mobileDriver = (InteractsWithApps) driver;
        if (mobileDriver instanceof AndroidDriver androidDriver && timeout != null) {
          androidDriver.terminateApp(appId, options(timeout));
        }
        else {
          mobileDriver.terminateApp(appId);
        }
      }
    );
  }

  private AndroidTerminateApplicationOptions options(Duration timeout) {
    return new AndroidTerminateApplicationOptions().withTimeout(timeout);
  }
}
