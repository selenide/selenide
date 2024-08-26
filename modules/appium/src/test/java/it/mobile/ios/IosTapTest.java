package it.mobile.ios;

import com.codeborne.selenide.appium.SelenideAppium;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.appium.SelenideAppium.$;


class IosTapTest extends BaseSwagLabsAppIosTest {
  @Test
  void testIosTap() {
    SelenideAppium.openIOSDeepLink("mydemoapprn://login");

    $(By.name("Login button")).tap();
    $(By.name("Username is required")).shouldBe(visible);
  }
}
