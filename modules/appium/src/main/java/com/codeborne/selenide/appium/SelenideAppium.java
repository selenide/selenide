package com.codeborne.selenide.appium;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.impl.ElementFinder;
import com.codeborne.selenide.impl.WebElementWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Collection;
import java.util.Set;

import static com.codeborne.selenide.WebDriverRunner.driver;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;

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
  public static void openIOSDeepLink(String deepLinkUrl) {
    if (!hasWebDriverStarted()) {
      launchApp();
    }
    deepLinkLauncher.openDeepLinkOnIos(AppiumDriverRunner.getIosDriver(), deepLinkUrl);
  }
  /**
   * Open a deep link for an Android application
   * @param deepLinkUrl - deep link url
   * @param appPackage - Android application package
   */
  public static void openAndroidDeepLink(String deepLinkUrl, String appPackage) {
    if (!hasWebDriverStarted()) {
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

  public static SelenideAppiumElement $x(String xpathExpression) {
    return $(By.xpath(xpathExpression), 0);
  }

  public static SelenideAppiumElement $(By seleniumSelector) {
    return $(seleniumSelector, 0);
  }

  public static SelenideAppiumElement $(By seleniumSelector, int index) {
    return ElementFinder.wrap(driver(), SelenideAppiumElement.class, null, seleniumSelector, index);
  }

  public static SelenideAppiumElement $(WebElement webElement) {
    return WebElementWrapper.wrap(SelenideAppiumElement.class, driver(), webElement);
  }

  public static SelenideAppiumCollection $$(By selector) {
    return new SelenideAppiumCollection(driver(), selector);
  }

  public static SelenideAppiumCollection $$(Collection<? extends WebElement> elements) {
    return new SelenideAppiumCollection(driver(), elements);
  }
}
