package it.mobile.android;

import com.codeborne.selenide.appium.SelenideAppiumElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.appium.AppiumSwipeOptions.left;
import static com.codeborne.selenide.appium.AppiumSwipeOptions.right;
import static com.codeborne.selenide.appium.SelenideAppium.$;

class AndroidSwipeTest extends BaseApiDemosTest {
  private final SelenideAppiumElement tabs = $(By.id("android:id/tabs"));

  @BeforeEach
  void openScrollableTabs() {
    $(By.xpath(".//*[@text='Views']")).click();
    $(By.xpath(".//*[@text='Tabs']")).scrollTo().click();
    $(By.xpath(".//*[@text='5. Scrollable']")).click();
  }

  @Test
  void testAndroidSwipeInsideContainer() {
    $(By.xpath(".//*[@text='TAB 12']"))
      .swipe(right(10).inside(tabs))
      .shouldBe(visible);
  }

  @Test
  void testAndroidSwipeBackInsideContainer() {
    $(By.xpath(".//*[@text='TAB 12']"))
      .swipe(right(10).inside(tabs));
    $(By.xpath(".//*[@text='TAB 1']"))
      .swipe(left(10).inside(tabs))
      .shouldBe(visible);
  }
}
