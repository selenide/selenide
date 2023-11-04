package it.mobile.ios;

import com.codeborne.selenide.appium.AppiumSelectors;
import com.codeborne.selenide.appium.SelenideAppium;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.appium.SelenideAppium.$;

class IosTypeTest extends BaseSwagLabsAppIosTest {

  @Test
  void iosType() {
    SelenideAppium.openIOSDeepLink("mydemoapprn://login");
    $(AppiumSelectors.byName("Username input field"))
      .shouldBe(visible)
      .type("abc")
      .shouldHave(text("abc"));
  }
}
