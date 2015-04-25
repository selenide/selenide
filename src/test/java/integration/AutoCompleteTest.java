package integration;

import com.codeborne.selenide.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class AutoCompleteTest extends IntegrationTest {
  @Before
  public void openTestPageWithAutocomplete() {
    openFile("autocomplete.html");
    $("h4").shouldBe(empty);
  }

  @Before
  public void setUp() {
    Configuration.fastSetValue = false;
  }

  @After
  public void tearDown() {
    Configuration.fastSetValue = false;
  }

  @Test
  public void setValueTriggersKeyboardEvents() {
    $("#tags").setValue("javasc");
    startTypingForAutocomplete();
  }

  @Test
  public void sendKeysTriggersKeyboardEvents() {
    $("#tags").sendKeys("javasc");
    startTypingForAutocomplete();
  }

  private void startTypingForAutocomplete() {
    $(".ui-autocomplete li").shouldHave(text("JavaScript")).click();
    $("#anyButton").click();

    $("h4").shouldHave(text("This is JavaScript!"));
  }
}
