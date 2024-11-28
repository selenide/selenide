package it.mobile.ios;

import com.codeborne.selenide.appium.AppiumClickOptions;
import com.codeborne.selenide.appium.SelenideAppium;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;

class IosClickOptionsTest extends BaseSwagLabsAppIosTest {
  @Test
  void testIosTap() {
    SelenideAppium.openIOSDeepLink("mydemoapprn://login");

    SelenideAppium.$(By.name("Login button")).click(AppiumClickOptions.tap());
    SelenideAppium.$(By.name("Username is required")).shouldBe(visible);
  }
}
