package it.mobile.android;

import com.codeborne.selenide.appium.SelenideAppium;
import io.appium.java_client.AppiumBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.appium.SelenideAppium.$;

class AndroidTypeTest extends BaseApiDemosTest {

  @BeforeEach
  void setUp() {
    closeWebDriver();
    SelenideAppium.launchApp();
  }

  @Test
  void androidType() {
    $(AppiumBy.xpath(".//*[@text='Views']")).tap();
    $(AppiumBy.xpath(".//*[@text='TextFields']"))
      .scrollTo()
      .shouldBe(visible)
      .click();
    $(AppiumBy.id("io.appium.android.apis:id/edit"))
      .shouldBe(visible)
      .type("abc")
      .shouldHave(text("abc"));
  }
}
