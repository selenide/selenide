package it.mobile.android;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.ClickOptions.usingDefaultMethod;
import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.ClickOptions.withTimeout;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.appium.AppiumClickOptions.doubleTap;
import static com.codeborne.selenide.appium.AppiumClickOptions.doubleTapWithOffset;
import static com.codeborne.selenide.appium.AppiumClickOptions.longPressFor;
import static com.codeborne.selenide.appium.AppiumClickOptions.longPressWithOffsetFor;
import static com.codeborne.selenide.appium.AppiumClickOptions.tap;
import static com.codeborne.selenide.appium.AppiumClickOptions.tapWithOffset;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AndroidClickOptionsTest extends BaseApiDemosTest {
  @Test
  void androidTap() {
    $(By.xpath(".//*[@text='Views']")).click(tap());
    $(By.xpath(".//*[@text='Animation']")).shouldBe(visible);
  }

  @Test
  void androidTapWithOffset() {
    // Find "Views" list item
    // But click on "Text" list item by calculating offset of list item height
    int heightOffset = $(By.xpath(".//*[@text='Views']"))
      .shouldBe(visible)
      .getSize()
      .getHeight();
    $(By.xpath(".//*[@text='Views']")).click(tapWithOffset(0, -heightOffset));
    $(By.xpath(".//*[@text='KeyEventText']")).shouldBe(visible);
  }

  @Test
  void androidLongPress() {
    $(By.xpath(".//*[@text='Views']")).click();
    $(By.xpath(".//*[@text='Expandable Lists']")).click();
    $(By.xpath(".//*[@text='1. Custom Adapter']")).click();
    $(By.xpath(".//*[@text='People Names']")).click(longPressFor(Duration.ofSeconds(4)));
    $(By.xpath(".//*[@text='Sample menu']")).shouldBe(visible);
  }

  @Test
  void androidLongPressWithOffset() {
    $(By.xpath(".//*[@text='Views']")).click();
    $(By.xpath(".//*[@text='Expandable Lists']")).click();
    $(By.xpath(".//*[@text='1. Custom Adapter']")).click();
    // Offset is close to the right edge of the element - it should still land on it if the base point is the
    // element's center (as it should be), but would miss it entirely if the base point were the top-left corner.
    SelenideElement peopleNames = $(By.xpath(".//*[@text='People Names']")).shouldBe(visible);
    int offsetX = peopleNames.getSize().getWidth() / 2 - 5;
    peopleNames.click(longPressWithOffsetFor(Duration.ofSeconds(4), offsetX, 0));
    $(By.xpath(".//*[@text='Sample menu']")).shouldBe(visible);
  }

  @Test
  void androidDoubleTap() {
    $(By.xpath(".//*[@text='Views']")).click();
    $(By.xpath(".//*[@text='TextSwitcher']")).scrollTo().click();
    $(By.xpath("//android.widget.Button")).click(doubleTap());
    $(By.xpath("(.//android.widget.TextView)[2]")).shouldHave(text("2"));
  }

  @Test
  void androidDoubleTapWithOffset() {
    $(By.xpath(".//*[@text='Views']")).click();
    $(By.xpath(".//*[@text='TextSwitcher']")).scrollTo().click();
    // Same reasoning as androidLongPressWithOffset: an offset close to the button's edge only lands on the
    // button if it's computed from the button's center.
    SelenideElement button = $(By.xpath("//android.widget.Button")).shouldBe(visible);
    int offsetX = button.getSize().getWidth() / 2 - 5;
    button.click(doubleTapWithOffset(offsetX, 0));
    $(By.xpath("(.//android.widget.TextView)[2]")).shouldHave(text("2"));
  }

  @Test
  void androidDoubleClick() {
    $(By.xpath(".//*[@text='Preference']")).click();
    $(By.xpath(".//*[@text='1. Preferences from XML']")).click();
    $(By.xpath(".//android.widget.CheckBox")).doubleClick();
    $(By.xpath(".//android.widget.CheckBox")).shouldHave(attribute("checked", "false"));
  }

  @Test
  void selenideClickOptions() {
    $(By.xpath(".//*[@text='Preference']")).click(usingDefaultMethod().offset(3, -3));
    $(By.xpath(".//*[@text='1. Preferences from XML']")).shouldBe(visible);
  }

  @Test
  void clickUsingJavascript_notSupported() {
    Configuration.timeout = 10;
    assertThatThrownBy(() -> $(By.xpath(".//*[@text='Preference']")).click(withTimeout(ofSeconds(2))))
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessage("Click timeout is not supported in mobile");
  }

  @Test
  void clickWithTimeout_notSupported() {
    Configuration.timeout = 10;
    assertThatThrownBy(() -> $(By.xpath(".//*[@text='Preference']")).click(usingJavaScript()))
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessage("Click using JavaScript is not supported in mobile");
  }
}
