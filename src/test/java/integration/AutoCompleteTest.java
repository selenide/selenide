package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.cssClass;
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
    waitUntilInputIsInitialized().setValue("javasc");
    verifyAutocomplete();
  }

  @Test
  public void sendKeysTriggersKeyboardEvents() {
    waitUntilInputIsInitialized().sendKeys("javasc");
    verifyAutocomplete();
  }

  private SelenideElement waitUntilInputIsInitialized() {
    return $("#tags").shouldHave(cssClass("ui-autocomplete-input"));
  }

  private void verifyAutocomplete() {
    System.out.println("=================");
    System.out.println($("#tags"));
    System.out.println("=================");
    $(".ui-autocomplete li").shouldHave(text("JavaScript")).click();
    $("#anyButton").click();

    $("h4").shouldHave(text("This is JavaScript!"));
  }
}
