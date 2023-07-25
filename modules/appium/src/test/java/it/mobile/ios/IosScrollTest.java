package it.mobile.ios;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.appium.SelenideAppium.$x;

class IosScrollTest extends BaseSwagLabsAppIosTest {

  @Test
  void testScrollToElementOnIos() {

    $x("//XCUIElementTypeStaticText[@name='© 2023 Sauce Labs. All Rights Reserved. Terms of Service | Privacy Policy.']")
      .scrollTo()
      .shouldBe(visible);
  }
}
