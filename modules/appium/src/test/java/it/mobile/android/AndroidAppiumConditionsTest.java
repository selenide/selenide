package it.mobile.android;

import com.codeborne.selenide.appium.SelenideAppium;
import io.appium.java_client.AppiumBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.appium.AppiumCollectionCondition.attributes;
import static com.codeborne.selenide.appium.AppiumCondition.attribute;
import static com.codeborne.selenide.appium.conditions.CombinedAttribute.android;
import static java.util.Arrays.asList;

class AndroidAppiumConditionsTest extends BaseApiDemosTest {

  @BeforeEach
  void setUp() {
    closeWebDriver();
    SelenideAppium.launchApp();
  }

  @Test
  void appiumCollectionConditionAttribute() {
    List<String> expectedAttributeValues = asList("API Demos", "KeyEventText", "Linkify", "LogTextBox", "Marquee", "Unicode");

    $(By.xpath(".//*[@text='Text']")).click();
    $$(By.xpath("//android.widget.TextView"))
      .shouldHave(attributes(android("text"), expectedAttributeValues));
  }

  @Test
  void appiumConditionAttribute() {
    $(AppiumBy.accessibilityId("Accessibility"))
      .shouldHave(attribute(android("content-desc").ios("name"), "Accessibility"));
  }
}
