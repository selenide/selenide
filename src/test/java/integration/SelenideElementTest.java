package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static org.assertj.core.api.Assertions.assertThat;

final class SelenideElementTest extends ITest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void selenideElementImplementsWrapsElement() {
    WebElement wrappedElement = $("#login").getWrappedElement();
    assertThat(wrappedElement).isNotNull();
    assertThat(wrappedElement.getAttribute("id")).isEqualTo("login");
  }

  @Test
  void selenideElementImplementsWrapsWebdriver() {
    WebDriver wrappedDriver = $("#login").getWrappedDriver();
    assertThat(wrappedDriver).isNotNull();
    String currentUrl = wrappedDriver.getCurrentUrl();
    assertThat(currentUrl).contains("page_with_selects_without_jquery.html");
  }

  @Test
  void selenideElementChainedWithByTextSelector() {
    $("#status").$(withText("Smith")).shouldBe(visible);
    $("#status").$(byText("Bob Smith")).shouldBe(visible);
  }
}
