package integration;

import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static org.junit.Assume.assumeFalse;

public class AutoCompleteTest extends IntegrationTest {
  @Before
  public void openTestPageWithAutocomplete() {
    openFile("autocomplete.html");
  }

  @Test
  public void setValueTriggersKeyboardEvents() {
    assumeFalse(isHtmlUnit());
    $("h4").shouldBe(empty);

    $("#tags").val("javasc");
    $(".ui-autocomplete li").shouldHave(text("JavaScript")).click();
    $("#void").click();
    
    $("h4").shouldHave(text("This is JavaScript!"));
  }
}
