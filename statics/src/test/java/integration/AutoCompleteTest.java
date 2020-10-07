package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Configuration.fastSetValue;
import static com.codeborne.selenide.Selenide.$;

@Disabled
  // this test works on my machine, but fails on Jenkins. Need to investigate it.
final class AutoCompleteTest extends IntegrationTest {
  @BeforeEach
  void openTestPageWithAutocomplete() {
    openFile("autocomplete.html");
    $("h4").shouldBe(empty);
  }

  @Test
  void sendKeysTriggersKeyboardEvents() {
    waitUntilInputIsInitialized().sendKeys("javasc");
    verifyAutocomplete();
  }

  private SelenideElement waitUntilInputIsInitialized() {
    return $("#tags").shouldHave(attribute("autocomplete", "off"), cssClass("ui-autocomplete-input"));
  }

  private void verifyAutocomplete() {
    $(".ui-autocomplete li").shouldHave(text("JavaScript")).click();
    $("#anyButton").click();

    $("h4").shouldHave(text("This is JavaScript!"));
  }

  @Test
  void setValueTriggersKeyboardEvents() {
    fastSetValue = false;
    waitUntilInputIsInitialized().setValue("javasc");
    verifyAutocomplete();
  }

  @Test
  void fastSetValueTriggersKeyboardEvents() {
    fastSetValue = true;
    waitUntilInputIsInitialized().setValue("javasc");
    verifyAutocomplete();
  }
}
