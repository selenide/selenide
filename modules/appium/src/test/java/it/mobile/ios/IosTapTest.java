package it.mobile.ios;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.appium.SelenideAppium.$;


class IosTapTest extends BaseIosCalculatorTest {
  @Test
  void testIosTap() {
    $(By.name("IntegerA")).sendKeys("3");
    $(By.name("IntegerB")).sendKeys("3");
    $(By.name("ComputeSumButton")).tap();
    $(By.name("Answer")).shouldHave(text("6"));
  }
}
