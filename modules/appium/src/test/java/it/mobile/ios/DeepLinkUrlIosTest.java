package it.mobile.ios;

import com.codeborne.selenide.appium.SelenideAppium;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;
import static com.codeborne.selenide.appium.AppiumSelectors.byAttribute;

class DeepLinkUrlIosTest extends BaseSwagLabsAppIosTest {
  @Test
  void deepLinkInIos() {
    SelenideAppium.openIOSDeepLink("mydemoapprn://product-details/1");
    $(byAttribute("label", "Sauce Labs Backpack")).shouldBe(visible, Duration.ofSeconds(20));
  }

  @AfterEach
  void tearDown() {
    if (hasWebDriverStarted()) {
      SelenideAppium.terminateApp("com.saucelabs.mydemoapp.rn");
      closeWebDriver();
    }
  }
}
