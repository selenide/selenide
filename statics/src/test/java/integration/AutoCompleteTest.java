package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Configuration.fastSetValue;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

final class AutoCompleteTest extends IntegrationTest {
  @BeforeEach
  void openTestPageWithAutocomplete() {
    openFile("autocomplete.html");
    $("h4").shouldBe(empty);
  }

  @Test
  void datalistOffersExpectedSuggestions() {
    $("#tags").shouldHave(attribute("list", "available-tags"));
    $$("#available-tags option").shouldHave(sizeGreaterThan(10));
    $("#available-tags option[value='JavaScript']").shouldHave(attribute("value", "JavaScript"));
  }

  @Test
  void setValueTriggersChangeEvent() {
    fastSetValue = false;
    $("#tags").setValue("JavaScript");
    $("#anyButton").click();
    $("h4").shouldHave(text("This is JavaScript!"));
  }

  @Test
  void fastSetValueTriggersChangeEvent() {
    fastSetValue = true;
    $("#tags").setValue("JavaScript");
    $("#anyButton").click();
    $("h4").shouldHave(text("This is JavaScript!"));
  }
}
