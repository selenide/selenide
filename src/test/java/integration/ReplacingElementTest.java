package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.partialValue;
import static com.codeborne.selenide.Condition.value;
import static org.assertj.core.api.Assertions.assertThat;

final class ReplacingElementTest extends ITest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_replacing_elements.html");
  }

  @Test
  void shouldWaitsUntilElementIsReplaced() {
    withLongTimeout(() -> {
      $("#dynamic-element").shouldHave(value("I will be replaced soon"));

      driver().executeJavaScript("replaceElement()");
      $("#dynamic-element").shouldHave(value("Hello, I am back"), cssClass("reloaded"));
      $("#dynamic-element").setValue("New value");
    });
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

  @RepeatedTest(100)
  void tryToCatchStaleElementException() {
    setTimeout(2000);
    SelenideElement dynamicElement = $("#dynamic-element");
    dynamicElement.shouldHave(value("I will be replaced soon"));

    $("#start-regular-replacement").click();
    for (int i = 0; i < 30; i++) {
      dynamicElement
        .shouldHave(partialValue("I am back"), cssClass("reloaded"))
        .setValue("New value from test");
    }
  }
}
