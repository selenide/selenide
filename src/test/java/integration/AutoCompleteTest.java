package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

// @Ignore // this test works on my machine, but fails on Jenkins. Need to investigate it.
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
