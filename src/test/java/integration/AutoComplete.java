package integration;

import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class AutoComplete extends IntegrationTest {
  @Before
  public void openTestPageWithAutocomplete() {
    openFile("autocomplete.html");
  }

  @Test
  public void setValueTriggersKeyboardEvents() {
    $("h4").shouldBe(empty);
    
    $("#tags").val("javasc");
    $(".ui-autocomplete li").shouldHave(text("JavaScript")).click();
    $("#tags").pressTab();
    
    $("h4").shouldHave(text("Yeah, JavaScript!"));
  }
}
