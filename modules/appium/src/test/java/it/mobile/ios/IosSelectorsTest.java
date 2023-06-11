package it.mobile.ios;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.appium.AppiumSelectors.*;

class IosSelectorsTest extends BaseIosCalculatorTest {
  @Test
  void appiumSelectorsInIosApp() {
    $(byTagAndName("*", "IntegerA")).setValue("2");
    $(byName("IntegerB")).setValue("4");
    $(withName("ComputeSum")).click();
    $(withTagAndName("*", "Answ")).shouldHave(text("6"));
    $(withTagAndName("XCUIElementTypeStaticText", "Answ")).shouldHave(text("6"));
    $(byTagAndName("XCUIElementTypeStaticText", "Answer")).shouldHave(text("6"));
  }
}
