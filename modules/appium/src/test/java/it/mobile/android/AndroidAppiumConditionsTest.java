package it.mobile.android;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.appium.SelenideAppium;
import com.codeborne.selenide.ex.TextsMismatch;
import io.appium.java_client.AppiumBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.appium.AppiumCollectionCondition.attributes;
import static com.codeborne.selenide.appium.AppiumCondition.attribute;
import static com.codeborne.selenide.appium.conditions.CombinedAttribute.android;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AndroidAppiumConditionsTest extends BaseApiDemosTest {

  @BeforeEach
  void setUp() {
    closeWebDriver();
    SelenideAppium.launchApp();
  }

  @Test
  void appiumCollectionConditionAttribute() {
    $(By.xpath(".//*[@text='Text']")).click();
    $$(By.xpath("//android.widget.TextView"))
      .shouldHave(attributes(android("text"), asList("API Demos", "KeyEventText", "Linkify", "LogTextBox", "Marquee", "Unicode")));
  }

  @Test
  void appiumCollectionConditionAttribute_listSizeMismatch() {
    $(By.xpath(".//*[@text='Text']")).click();
    ElementsCollection elements = $$(By.xpath("//android.widget.TextView"));
    elements.shouldHave(sizeGreaterThan(0));

    Configuration.timeout = 1;
    List<String> expectedAttributes = asList("API Demos", "KeyEventText", "Linkify", "LogTextBox");
    assertThatThrownBy(() -> elements.shouldHave(attributes(android("text"), expectedAttributes)))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith("List size mismatch (expected: 4, actual: 6)")
      .hasMessageContaining("Actual (6): [API Demos, KeyEventText, Linkify, LogTextBox, Marquee, Unicode]")
      .hasMessageContaining("Expected (4): [API Demos, KeyEventText, Linkify, LogTextBox]")
      .hasMessageContaining("Collection: By.xpath: //android.widget.TextView");
  }

  @Test
  void appiumCollectionConditionAttribute_textsMismatch() {
    $(By.xpath(".//*[@text='Text']")).click();
    ElementsCollection elements = $$(By.xpath("//android.widget.TextView"));
    elements.shouldHave(sizeGreaterThan(0));

    Configuration.timeout = 1;
    List<String> expectedAttributes = asList("API Demos", "KeyEventText", "Linkify", "LogTextBox", "WRONG", "Unicode");
    assertThatThrownBy(() -> elements.shouldHave(attributes(android("text"), expectedAttributes)))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith("Attribute #4 mismatch (expected: \"WRONG\", actual: \"Marquee\")")
      .hasMessageContaining("Actual (6): [API Demos, KeyEventText, Linkify, LogTextBox, Marquee, Unicode]")
      .hasMessageContaining("Expected (6): [API Demos, KeyEventText, Linkify, LogTextBox, WRONG, Unicode]")
      .hasMessageContaining("Collection: By.xpath: //android.widget.TextView");
  }

  @Test
  void appiumConditionAttribute() {
    $(AppiumBy.accessibilityId("Accessibility"))
      .shouldHave(attribute(android("content-desc").ios("name"), "Accessibility"));
  }
}
