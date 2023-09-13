package it.mobile.android;

import com.codeborne.selenide.appium.SelenideAppium;
import io.appium.java_client.AppiumBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.appium.AppiumClickOptions.longPressFor;
import static com.codeborne.selenide.appium.AppiumClickOptions.tapWithOffset;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static java.time.Duration.ofSeconds;

class AndroidTapTest extends BaseApiDemosTest {
  @BeforeEach
  void setUp() {
    closeWebDriver();
    SelenideAppium.launchApp();
  }

  @Test
  void androidTap() {
    $(AppiumBy.xpath(".//*[@text='Views']")).tap();
    $(AppiumBy.xpath(".//*[@text='Animation']")).shouldBe(visible);
  }

  @Test
  void androidTapWithOffset() {
    $(AppiumBy.xpath(".//*[@text='Views']")).tap(tapWithOffset(0, -200)); //Find view but click Text
    $(AppiumBy.xpath(".//*[@text='KeyEventText']")).shouldBe(visible);
  }

  @Test
  void androidLongPress() {
    $(AppiumBy.xpath(".//*[@text='Views']")).click();
    $(AppiumBy.xpath(".//*[@text='Expandable Lists']")).click();
    $(AppiumBy.xpath(".//*[@text='1. Custom Adapter']")).click();
    $(AppiumBy.xpath(".//*[@text='People Names']")).tap(longPressFor(ofSeconds(4)));
    $(AppiumBy.xpath(".//*[@text='Sample menu']")).shouldBe(visible);
  }

  @Test
  void androidDoubleTap() {
    $(AppiumBy.xpath(".//*[@text='Views']")).click();
    $(AppiumBy.xpath(".//*[@text='TextSwitcher']")).scrollTo().click();
    $(AppiumBy.xpath("//android.widget.Button")).doubleTap();
    $(AppiumBy.xpath("(.//android.widget.TextView)[2]")).shouldHave(text("2"));
  }
}
