package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.fastSetValue;
import static com.codeborne.selenide.Selenide.$;

public class AutoCompleteTest extends IntegrationTest {
  @Before
  public void openTestPageWithAutocomplete() {
    openFile("autocomplete.html");
    $("h4").shouldBe(empty);
  }

  @After
  public void tearDown() {
    fastSetValue = false;
  }

  @Test
  public void sendKeysTriggersKeyboardEvents() {
    waitUntilInputIsInitialized().sendKeys("javasc");
    verifyAutocomplete();
  }

  @Test
  public void setValueTriggersKeyboardEvents() {
    fastSetValue = false;
    waitUntilInputIsInitialized().setValue("javasc");
    verifyAutocomplete();
  }

  @Test
  public void fastSetValueTriggersKeyboardEvents() {
    fastSetValue = true;
    waitUntilInputIsInitialized().setValue("javasc");
    verifyAutocomplete();
  }

  private SelenideElement waitUntilInputIsInitialized() {
    System.out.println("-----------------");
    System.out.println($("#tags"));
    System.out.println("-----------------");
    return $("#tags").shouldHave(attribute("autocomplete", "off"), cssClass("ui-autocomplete-input"));
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
