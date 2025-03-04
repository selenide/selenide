package com.codeborne.selenide.appium;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;

import java.time.Duration;
import java.util.Map;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static com.codeborne.selenide.impl.WebdriverUnwrapper.cast;
import static io.appium.java_client.AppiumBy.iOSNsPredicateString;
import static io.appium.java_client.ios.options.wda.SupportsAutoAcceptAlertsOption.AUTO_ACCEPT_ALERTS_OPTION;

public class DeepLinkLauncher {
  public static Duration SAFARI_ELEMENTS_TIMEOUT = Duration.ofSeconds(1);
  private static final String SAFARI_BUNDLE_ID = "com.apple.mobilesafari";

  // adopted from https://bit.ly/3OKVsvq
  // see also https://appiumpro.com/editions/84-reliably-opening-deep-links-across-platforms-and-devices
  public void openDeepLinkOnIos(IOSDriver driver, String deepLinkUrl) {
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
  public void openDeepLinkOnAndroid(AppiumDriver driver, String deepLinkUrl, String appPackage) {
    SelenideLogger.run("open android deeplink", deepLinkUrl, () -> {
      Map<String, String> params = ImmutableMap.of(
        "url", deepLinkUrl,
        "package", appPackage
      );
      driver.executeScript("mobile: deepLink", params);
    });
  }

  private void openSafari(AppiumDriver driver) {
    driver.executeScript(
      "mobile:launchApp",
      ImmutableMap.of("bundleId", SAFARI_BUNDLE_ID)
    );
  }

  private boolean canAutoAcceptAlerts(AppiumDriver driver) {
    HasCapabilities hasCapabilities = cast(driver, HasCapabilities.class)
      .orElseThrow(() -> new IllegalArgumentException("Driver doesn't support HasCapabilities"));
    Capabilities capabilities = hasCapabilities.getCapabilities();
    return capabilities.is(AUTO_ACCEPT_ALERTS_OPTION);
  }
}
