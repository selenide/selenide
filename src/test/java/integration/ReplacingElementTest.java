package integration;

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
    assertThat($("#dynamic-element").innerText())
      .isEmpty();
  }

  @Test
  void getInnerHtml() {
    assertThat($("#dynamic-element").innerHtml())
      .isEmpty();
  }

  @Test
  void findAll() {
    $("#dynamic-element").findAll(".child").shouldBe(empty);
  }

  @Test
  void testToString() {
    assertThat($("#dynamic-element"))
      .hasToString("<input id=\"dynamic-element\" type=\"text\" value=\"I will be replaced soon\"></input>");
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
