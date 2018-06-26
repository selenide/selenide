package integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

class ReplacingElementTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_replacing_elements.html");
  }

  @Test
  void shouldWaitsUntilElementIsReplaced() {
    $("#dynamic-element").shouldHave(value("I will be replaced soon"));

    executeJavaScript("replaceElement()");
    $("#dynamic-element").shouldHave(value("Hello, I am back"), cssClass("reloaded"));
    $("#dynamic-element").setValue("New value");
  }

  @Test
  void getInnerText() {
    Assertions.assertEquals("", $("#dynamic-element").innerText());
  }

  @Test
  void getInnerHtml() {
    Assertions.assertEquals("", $("#dynamic-element").innerHtml());
  }

  @Test
  void findAll() {
    $("#dynamic-element").findAll(".child").shouldBe(empty);
  }

  @Test
  void testToString() {
    Assertions.assertEquals("<input id=\"dynamic-element\" type=\"text\" value=\"I will be replaced soon\"></input>",
      $("#dynamic-element").toString());
  }

  @Test
  @Disabled
  void tryToCatchStaleElementException() {
    executeJavaScript("startRegularReplacement()");
    for (int i = 0; i < 10; i++) {
      $("#dynamic-element").shouldHave(value("I am back"), cssClass("reloaded")).setValue("New value from test");
    }
  }
}
