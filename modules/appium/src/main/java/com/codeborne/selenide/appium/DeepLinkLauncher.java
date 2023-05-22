package com.codeborne.selenide.appium;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HasOnScreenKeyboard;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.CheckReturnValue;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.Map;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.appium.WebdriverUnwrapper.cast;
import static io.appium.java_client.AppiumBy.iOSNsPredicateString;
import static io.appium.java_client.ios.options.wda.SupportsAutoAcceptAlertsOption.AUTO_ACCEPT_ALERTS_OPTION;

@ParametersAreNonnullByDefault
public class DeepLinkLauncher {
  public static Duration SAFARI_ELEMENTS_TIMEOUT = Duration.ofSeconds(10);
  private static final String SAFARI_BUNDLE_ID = "com.apple.mobilesafari";
  private static final Logger log = LoggerFactory.getLogger(DeepLinkLauncher.class);

  // adopted from https://bit.ly/3OKVsvq
  // see also https://appiumpro.com/editions/84-reliably-opening-deep-links-across-platforms-and-devices
  public void openDeepLinkOnIos(AppiumDriver driver, String deepLinkUrl) {
    SelenideLogger.run("open ios deeplink", deepLinkUrl, () -> {
      HasOnScreenKeyboard keyboard = cast(driver, HasOnScreenKeyboard.class)
        .orElseThrow(() -> new IllegalArgumentException("Driver doesn't support HasOnScreenKeyboard"));
      openSafari(driver);
      // Add the deep link url in Safari in the `URL`-field
      // This can be 2 different elements, or the button, or the text field
      // Use the predicate string because the accessibility label will return 2 different types
      // of elements making it flaky to use. With predicate string we can be more precise
      SelenideElement addressBar = $(iOSNsPredicateString("label == 'Address' OR label == 'Адрес' OR name == 'URL'"));
      SelenideElement urlField = $(iOSNsPredicateString("type == 'XCUIElementTypeTextField' && name CONTAINS 'URL'"));
      // Wait for the url button to appear and click on it so the text field will appear
      // iOS 13 now has the keyboard open by default because the URL field has focus when opening the Safari browser
      if (!keyboard.isKeyboardShown()) {
        addressBar.click();
      }
      // Submit the url and add a break
      urlField.shouldBe(visible, SAFARI_ELEMENTS_TIMEOUT).setValue(deepLinkUrl + "\uE007");
      // if you started the iOS device with `autoAcceptAlerts:true` in the capabilities
      // then Appium will auto accept the alert that should be shown now.
      if (!canAutoAcceptAlerts(driver)) {
        // Wait for the notification and accept it
        // When using an iOS simulator you will only get the pop-up once, all the other times it won't be shown
        try {
          SelenideElement openButton =
            $(iOSNsPredicateString("type == 'XCUIElementTypeButton' && (name CONTAINS 'Open' OR name CONTAINS 'Открыть')"));
          openButton.shouldBe(enabled, SAFARI_ELEMENTS_TIMEOUT).click();
        } catch (AssertionError openButtonNotFound) {
          log.warn("Open button not found", openButtonNotFound);
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
      driver.executeScript("mobile:deepLink", params);
    });
  }

  private void openSafari(AppiumDriver driver) {
    driver.executeScript(
      "mobile:launchApp",
      ImmutableMap.of("bundleId", SAFARI_BUNDLE_ID)
    );
  }

  @CheckReturnValue
  private boolean canAutoAcceptAlerts(AppiumDriver driver) {
    HasCapabilities hasCapabilities = cast(driver, HasCapabilities.class)
      .orElseThrow(() -> new IllegalArgumentException("Driver doesn't support HasCapabilities"));
    Capabilities capabilities = hasCapabilities.getCapabilities();
    return capabilities.is(AUTO_ACCEPT_ALERTS_OPTION);
  }
}
