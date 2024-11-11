package it.mobile.ios;

import com.codeborne.selenide.appium.SelenideAppium;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.appium.AppiumSelectors.byTagAndName;
import static com.codeborne.selenide.appium.AppiumSelectors.withName;
import static com.codeborne.selenide.appium.AppiumSelectors.withTagAndName;

class IosSelectorsTest extends BaseSwagLabsAppIosTest {
  @Test
  void appiumSelectorsInIosApp() {
    SelenideAppium.openIOSDeepLink("mydemoapprn://login");
    $(byTagAndName("*", "Username input field")).setValue("2");
    $(withTagAndName("*", "Password input")).setValue("3");
    $(withName("Login button")).click();
    $(withTagAndName("XCUIElementTypeOther", "generic-error"))
      .shouldHave(text("Provided credentials do not match any user in this service."));
    $(byTagAndName("XCUIElementTypeOther", "generic-error-message"))
      .shouldHave(text("Provided credentials do not match any user in this service."));
  }
}
