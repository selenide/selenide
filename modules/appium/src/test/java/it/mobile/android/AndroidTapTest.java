package it.mobile.android;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.appium.AppiumClickOptions.longPressFor;
import static com.codeborne.selenide.appium.AppiumClickOptions.tapWithOffset;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static java.time.Duration.ofSeconds;

class AndroidTapTest extends BaseApiDemosTest {
  @Test
  void androidTap() {
    $(By.xpath(".//*[@text='Views']")).tap();
    $(By.xpath(".//*[@text='Animation']")).shouldBe(visible);
  }

  @Test
  void androidTapWithOffset() {
    // Find "Views" list item
    // But tap on "Text" list item by calculating offset of list item height
    int heightOffset = $(By.xpath(".//*[@text='Views']"))
      .shouldBe(visible)
      .getSize()
      .getHeight();
    $(By.xpath(".//*[@text='Views']")).tap(tapWithOffset(0, -heightOffset)); //Find view but click Text
    $(By.xpath(".//*[@text='KeyEventText']")).shouldBe(visible);
  }

  @Test
  void androidLongPress() {
    $(By.xpath(".//*[@text='Views']")).click();
    $(By.xpath(".//*[@text='Expandable Lists']")).click();
    $(By.xpath(".//*[@text='1. Custom Adapter']")).click();
    $(By.xpath(".//*[@text='People Names']")).tap(longPressFor(ofSeconds(4)));
    $(By.xpath(".//*[@text='Sample menu']")).shouldBe(visible);
  }

  @Test
  void androidDoubleTap() {
    $(By.xpath(".//*[@text='Views']")).click();
    $(By.xpath(".//*[@text='TextSwitcher']")).scrollTo().click();
    $(By.xpath("//android.widget.Button")).doubleTap();
    $(By.xpath("(.//android.widget.TextView)[2]")).shouldHave(text("2"));
  }
}
