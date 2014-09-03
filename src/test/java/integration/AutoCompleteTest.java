package integration;

import com.codeborne.selenide.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static org.junit.Assume.assumeFalse;

public class AutoCompleteTest extends IntegrationTest {
  @Before
  public void openTestPageWithAutocomplete() {
    openFile("autocomplete.html");
  }

  @After
  public void tearDown() {
    Configuration.fastSetValue = false;
  }

  @Test
  public void setValueTriggersKeyboardEvents() {
    assumeFalse(isHtmlUnit());
    startTypingForAutocomplete();
  }

  @Test
  public void setValueTriggersKeyboardEvents_fast_mode() {
    assumeFalse(isHtmlUnit() || isFirefox());
    Configuration.fastSetValue = true;
    startTypingForAutocomplete();
  }

  private void startTypingForAutocomplete() {
    $("h4").shouldBe(empty);

    $("#tags").val("javasc");
    $(".ui-autocomplete li").shouldHave(text("JavaScript")).click();
    $("#anyButton").click();

    $("h4").shouldHave(text("This is JavaScript!"));
  }
}
