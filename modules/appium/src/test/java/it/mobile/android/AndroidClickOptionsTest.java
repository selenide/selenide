package it.mobile.android;

import com.codeborne.selenide.appium.SelenideAppium;
import io.appium.java_client.AppiumBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.ClickOptions.usingDefaultMethod;
import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.ClickOptions.withTimeout;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.appium.AppiumClickOptions.doubleTap;
import static com.codeborne.selenide.appium.AppiumClickOptions.longPressFor;
import static com.codeborne.selenide.appium.AppiumClickOptions.tap;
import static com.codeborne.selenide.appium.AppiumClickOptions.tapWithOffset;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AndroidClickOptionsTest extends BaseApiDemosTest {
  @BeforeEach
  void setUp() {
    closeWebDriver();
    SelenideAppium.launchApp();
  }

  @Test
  void androidTap() {
    $(AppiumBy.xpath(".//*[@text='Views']")).click(tap());
    $(AppiumBy.xpath(".//*[@text='Animation']")).shouldBe(visible);
  }

  @Test
  void androidTapWithOffset() {
    $(AppiumBy.xpath(".//*[@text='Views']")).click(tapWithOffset(0, -200)); //Find view but click Text
    $(AppiumBy.xpath(".//*[@text='KeyEventText']")).shouldBe(visible);
  }

  @Test
  void androidLongPress() {
    $(AppiumBy.xpath(".//*[@text='Views']")).click();
    $(AppiumBy.xpath(".//*[@text='Expandable Lists']")).click();
    $(AppiumBy.xpath(".//*[@text='1. Custom Adapter']")).click();
    $(AppiumBy.xpath(".//*[@text='People Names']")).click(longPressFor(Duration.ofSeconds(4)));
    $(AppiumBy.xpath(".//*[@text='Sample menu']")).shouldBe(visible);
  }

  @Test
  void androidDoubleTap() {
    $(AppiumBy.xpath(".//*[@text='Views']")).click();
    $(AppiumBy.xpath(".//*[@text='TextSwitcher']")).scrollTo().click();
    $(AppiumBy.xpath("//android.widget.Button")).click(doubleTap());
    $(AppiumBy.xpath("(.//android.widget.TextView)[2]")).shouldHave(text("2"));
  }

  @Test
  void androidDoubleClick() {
    $(AppiumBy.xpath(".//*[@text='Preference']")).click();
    $(AppiumBy.xpath(".//*[@text='1. Preferences from XML']")).click();
    $(AppiumBy.xpath(".//android.widget.CheckBox")).doubleClick();
    $(AppiumBy.xpath(".//android.widget.CheckBox")).shouldHave(attribute("checked", "false"));
  }

  @Test
  void selenideClickOptions() {
    $(AppiumBy.xpath(".//*[@text='Preference']")).click(usingDefaultMethod().offset(3, -3));
    $(AppiumBy.xpath(".//*[@text='1. Preferences from XML']")).shouldBe(visible);
  }

  @Test
  void clickUsingJavascript_notSupported() {
    assertThatThrownBy(() -> $(AppiumBy.xpath(".//*[@text='Preference']")).click(withTimeout(ofSeconds(2))))
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessage("Click timeout is not supported in mobile");
  }

  @Test
  void clickWithTimeout_notSupported() {
    assertThatThrownBy(() -> $(AppiumBy.xpath(".//*[@text='Preference']")).click(usingJavaScript()))
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessage("Click using JavaScript is not supported in mobile");
  }
}
