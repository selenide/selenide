package it.mobile.ios;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.appium.SelenideAppium.$x;

class IosScrollTest extends BaseSwagLabsAppIosTest {

  @Test
  void testScrollToElementOnIos() {

    $x("//XCUIElementTypeStaticText[contains(@name, 'Sauce Labs. All Rights Reserved')]")
      .scrollTo()
      .shouldBe(visible);
  }
}
