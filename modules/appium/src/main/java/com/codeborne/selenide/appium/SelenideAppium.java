package com.codeborne.selenide.appium;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.impl.ElementFinder;
import org.openqa.selenium.By;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Set;

import static com.codeborne.selenide.WebDriverRunner.driver;

/**
 * The main starting point of Selenide-Appium.
 * <p>
 * You start with methods {@link #launchApp()} for launching the tested application
 */
public class SelenideAppium {
  private static final AppiumNavigator appiumNavigator = new AppiumNavigator();
  private static final DeepLinkLauncher deepLinkLauncher = new DeepLinkLauncher();

  private SelenideAppium() {

  }

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

  public static SelenideAppiumTargetLocator switchTo() {
    return new SelenideAppiumTargetLocator(WebDriverRunner.driver());
  }

  public static Set<String> getContextHandles() {
    return switchTo().getContextHandles();
  }

  public static String getCurrentContext() {
    return switchTo().getCurrentContext();
  }

  @CheckReturnValue
  @Nonnull
  public static SelenideAppiumElement $x(String xpathExpression) {
    return $(By.xpath(xpathExpression), 0);
  }

  @CheckReturnValue
  @Nonnull
  public static SelenideAppiumElement $(By seleniumSelector) {
    return $(seleniumSelector, 0);
  }

  @CheckReturnValue
  @Nonnull
  public static SelenideAppiumElement $(By seleniumSelector, int index) {
    return ElementFinder.wrap(driver(), SelenideAppiumElement.class, null, seleniumSelector, index);
  }
}
