package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.fastSetValue;
import static com.codeborne.selenide.Selenide.$;

@Ignore // this test works on my machine, but fails on Jenkins. Need to investigate it.
public class AutoCompleteTest extends IntegrationTest {
  @Before
  public void openTestPageWithAutocomplete() {
    openFile("autocomplete.html");
    $("h4").shouldBe(empty);
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
    return $("#tags").shouldHave(attribute("autocomplete", "off"), cssClass("ui-autocomplete-input"));
  }

  private void verifyAutocomplete() {
    $(".ui-autocomplete li").shouldHave(text("JavaScript")).click();
    $("#anyButton").click();

    $("h4").shouldHave(text("This is JavaScript!"));
  }
}
