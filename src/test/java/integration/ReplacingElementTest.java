package integration;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static org.junit.Assert.assertEquals;

public class ReplacingElementTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("page_with_replacing_elements.html");
  }

  @Test
  public void shouldWaitsUntilElementIsReplaced() {
    $("#dynamic-element").shouldHave(value("I will be replaced soon"));
    
    executeJavaScript("replaceElement()");
    $("#dynamic-element").shouldHave(value("Hello, I am back"), cssClass("reloaded"));
    $("#dynamic-element").setValue("New value");
  }

  @Test
  public void getInnerText() {
    assertEquals("", $("#dynamic-element").innerText());
  }

  @Test
  public void getInnerHtml() {
    assertEquals("", $("#dynamic-element").innerHtml());
  }

  @Test
  public void findAll() {
    $("#dynamic-element").findAll(".child").shouldBe(empty);
  }

  @Test
  public void testToString() {
    assertEquals("<input id=\"dynamic-element\" value=\"I will be replaced soon\" type=\"text\"></input>", 
        $("#dynamic-element").toString());
  }

  @Test @Ignore
  public void tryToCatchStaleElementException() {
    executeJavaScript("startRegularReplacement()");
    for (int i = 0; i < 10; i++) {
      $("#dynamic-element").shouldHave(value("I am back"), cssClass("reloaded")).setValue("New value from test");
    }
  }
}
