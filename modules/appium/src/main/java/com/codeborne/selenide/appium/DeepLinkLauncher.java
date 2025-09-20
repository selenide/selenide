package com.codeborne.selenide.appium;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.Map;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static io.appium.java_client.AppiumBy.iOSNsPredicateString;
import static io.appium.java_client.ios.options.wda.SupportsAutoAcceptAlertsOption.AUTO_ACCEPT_ALERTS_OPTION;

public class DeepLinkLauncher {
  public static Duration SAFARI_ELEMENTS_TIMEOUT = Duration.ofSeconds(1);
  private static final String SAFARI_BUNDLE_ID = "com.apple.mobilesafari";

  // adopted from https://bit.ly/3OKVsvq
  // see also https://appiumpro.com/editions/84-reliably-opening-deep-links-across-platforms-and-devices
  public void openDeepLinkOnIos(WebDriver driver, String deepLinkUrl) {
    SelenideLogger.run("open ios deeplink", deepLinkUrl, () -> {
      openSafari(driver);
      driver.navigate().to(deepLinkUrl);

      // if you started the iOS device with `autoAcceptAlerts:true` in the capabilities
      // then Appium will auto accept the alert that should be shown now.
      if (!canAutoAcceptAlerts(driver)) {
        // Wait for the notification and accept it
        // When using an iOS simulator you will only get the pop-up once, all the other times it won't be shown
        SelenideElement openButton =
          $(iOSNsPredicateString("type == 'XCUIElementTypeButton' && (name CONTAINS 'Open' OR name CONTAINS 'Открыть')"));
        if (openButton.is(enabled, SAFARI_ELEMENTS_TIMEOUT)) {
          openButton.click();
        }
      }
    });
  }

  // Life is so much easier
  public void openDeepLinkOnAndroid(WebDriver driver, String deepLinkUrl, String appPackage) {
    SelenideLogger.run("open android deeplink", deepLinkUrl, () -> {
      Map<String, String> params = Map.of(
        "url", deepLinkUrl,
        "package", appPackage
      );
      //noinspection UnnecessaryLabelJS,JSUnresolvedReference
      ((JavascriptExecutor) driver).executeScript("mobile: deepLink", params);
    });
  }

  private void openSafari(WebDriver driver) {
    //noinspection UnnecessaryLabelJS,JSUnresolvedReference
    ((JavascriptExecutor) driver).executeScript(
      "mobile:launchApp",
      Map.of("bundleId", SAFARI_BUNDLE_ID)
    );
  }

  private boolean canAutoAcceptAlerts(WebDriver driver) {
    return ((HasCapabilities) driver).getCapabilities().is(AUTO_ACCEPT_ALERTS_OPTION);
  }
}
