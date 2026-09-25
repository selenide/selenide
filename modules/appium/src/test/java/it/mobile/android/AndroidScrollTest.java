package it.mobile.android;


import com.codeborne.selenide.appium.AppiumScrollOptions;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.appium.AppiumScrollOptions.down;
import static com.codeborne.selenide.appium.AppiumScrollOptions.up;
import static com.codeborne.selenide.appium.AppiumScrollOptions.with;
import static com.codeborne.selenide.appium.ScrollDirection.DOWN;
import static com.codeborne.selenide.appium.SelenideAppium.$;

class AndroidScrollTest extends BaseApiDemosTest {

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

  @Test
  void testAndroidScrollInsideContainer() {
    $(By.xpath(".//*[@text='Views']")).click();
    SelenideAppiumElement list = $(By.id("android:id/list"));
    $(By.xpath(".//*[@text='Tabs']"))
      .scroll(down().inside(list))
      .shouldBe(visible);
  }

  @Test
  void testAndroidScrollUpInsideContainer() {
    $(By.xpath(".//*[@text='Views']")).click();
    SelenideAppiumElement list = $(By.id("android:id/list"));
    $(By.xpath(".//*[@text='Tabs']"))
      .scroll(down().inside(list));
    $(By.xpath(".//*[@text='Animation']"))
      .scroll(up(0.2f, 0.7f).inside(list))
      .shouldBe(visible);
  }
}
