package it.mobile.android;


import com.codeborne.selenide.appium.AppiumScrollOptions;
import com.codeborne.selenide.appium.SelenideAppium;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.appium.AppiumScrollOptions.up;
import static com.codeborne.selenide.appium.AppiumScrollOptions.with;
import static com.codeborne.selenide.appium.ScrollDirection.DOWN;
import static com.codeborne.selenide.appium.SelenideAppium.$;

class AndroidScrollTest extends BaseApiDemosTest {

  @BeforeEach
  void setUp() {
    closeWebDriver();
    SelenideAppium.launchApp();
  }

  @Test
  void testScrollToElement() {
    $(By.xpath(".//*[@text='Views']")).click();
    $(By.xpath(".//*[@text='Tabs']")).scrollTo().click();
    $(By.xpath(".//*[@text='1. Content By Id']"))
      .shouldBe(visible);
  }

  @Test
  void testAndroidScrollOptions() {
    $(By.xpath(".//*[@text='Views']")).click();
    $(By.xpath(".//*[@text='Tabs']"))
      .scroll(with(DOWN, 10));
    $(By.xpath(".//*[@text='Animation']"))
      .scroll(up())
      .shouldBe(visible);
  }

  @Test
  void testAndroidScrollOptionsWithCustomCoordinates() {
    $(By.xpath(".//*[@text='Views']")).click();
    $(By.xpath(".//*[@text='Tabs']"))
      .scroll(AppiumScrollOptions.down(0.15f, 0.60f))
      .shouldBe(visible);
  }
}
