package it.mobile.android;

import com.codeborne.selenide.appium.SelenideAppium;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;
import static com.codeborne.selenide.appium.AppiumSelectors.withText;

class DeepLinkUrlAndroidTest extends BaseSwagLabsAndroidTest {
  @Test
  void deepLinkInAndroid() {
    SelenideAppium.openAndroidDeepLink("mydemoapprn://product-details/1", "com.saucelabs.mydemoapp.rn");
    $(withText("Sauce Labs Backpack")).shouldBe(visible);
  }

  @AfterEach
  void tearDown() {
    if (hasWebDriverStarted()) {
      SelenideAppium.terminateApp("com.saucelabs.mydemoapp.rn");
    }
  }
}
