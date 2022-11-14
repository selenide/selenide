package com.codeborne.selenide.appium.ios_test_app;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.appium.AppiumSelectors.*;

class IosSelectorsTest {

  @Test
  void testAppiumSelectorsInIosApp() {
    closeWebDriver();
    Configuration.browser = IosTestAppDriverFactory.class.getName();
    Configuration.browserSize = null;
    open();
    $(byTagAndName("*","IntegerA")).setValue("2");
    $(byName("IntegerB")).setValue("4");
    $(withName("ComputeSum")).click();
    $(withTagAndName("*","Answ"))
      .shouldHave(text("6"));
  }
}
