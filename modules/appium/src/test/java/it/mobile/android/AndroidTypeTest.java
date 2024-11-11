package it.mobile.android;

import io.appium.java_client.AppiumBy;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.appium.SelenideAppium.$;

class AndroidTypeTest extends BaseApiDemosTest {

  @Test
  void androidType() {
    $(By.xpath(".//*[@text='Views']")).tap();
    $(By.xpath(".//*[@text='TextFields']"))
      .scrollTo()
      .shouldBe(visible)
      .click();
    $(AppiumBy.id("io.appium.android.apis:id/edit"))
      .shouldBe(visible)
      .type("abc")
      .shouldHave(text("abc"));
  }
}
