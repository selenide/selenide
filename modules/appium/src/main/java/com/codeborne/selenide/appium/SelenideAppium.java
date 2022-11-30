package com.codeborne.selenide.appium;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import javax.annotation.Nonnull;

/**
 * The main starting point of Selenide-Appium.
 * <p>
 * You start with methods {@link #launchApp()} for launching the tested application
 */
public class SelenideAppium {
  private static final AppiumNavigator appiumNavigator = new AppiumNavigator();
  private static final DeepLinkLauncher deepLinkLauncher = new DeepLinkLauncher();

  /**
   * The main starting point in your tests.
   * Launch a mobile application. Do nothing if driver already created.
   */
  public static void launchApp() {
    appiumNavigator.launchApp(() -> WebDriverRunner::getAndCheckWebDriver);
  }

  /**
   * Open a deep link for an IOS application
   * @param deepLinkUrl - deep link url
   */
  public static void openIOSDeepLink(@Nonnull String deepLinkUrl) {
    if (!WebDriverRunner.hasWebDriverStarted()) {
      launchApp();
    }
    deepLinkLauncher.openDeepLinkOnIos(AppiumDriverRunner.getMobileDriver(), deepLinkUrl);
  }
  /**
   * Open a deep link for an Android application
   * @param deepLinkUrl - deep link url
   * @param appPackage - Android application package
   */
  public static void openAndroidDeepLink(@Nonnull String deepLinkUrl, @Nonnull String appPackage) {
    if (!WebDriverRunner.hasWebDriverStarted()) {
      launchApp();
    }
    deepLinkLauncher.openDeepLinkOnAndroid(AppiumDriverRunner.getMobileDriver(), deepLinkUrl, appPackage);
  }

  /**
   * Terminate application
   * @param appId - applicationId for Android or bundleId for iOS
   */
  public static void terminateApp(String appId) {
    appiumNavigator.terminateApp(AppiumDriverRunner.getMobileDriver(), appId);
  }

  /**
   * Navigate app back to previous screen
   */
  public static void back() {
    Selenide.back();
  }
}
