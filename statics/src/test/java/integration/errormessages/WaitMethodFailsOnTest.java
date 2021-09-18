package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.have;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

final class WaitMethodFailsOnTest extends IntegrationTest {
  @BeforeEach
  void openPage() {
    givenHtml(
      "<ul>Hello to:",
      "<li class='the-expanse detective'>Miller <label>detective</label></li>",
      "<li class='the-expanse missing'>Julie Mao</li>",
      "</ul>"
    );
    Configuration.timeout = 1;
  }

  @Test
  void wait_until_text() {
    SelenideElement element = $(".detective");

    try {
      element.waitUntil(have(text("Müller")), 42);
      fail("Expected ElementNotFound");
    }
    catch (ElementShould expected) {
      assertThat(expected).hasMessageStartingWith("Element should have text 'Müller' {.detective}");
    }
  }

  @Test
  void wait_until_visible() {
    SelenideElement element = $$("ul .nonexistent").get(1);

    try {
      element.waitUntil(visible, 42);
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected).hasMessageStartingWith("Element not found {ul .nonexistent[1]}");
      assertThat(expected).hasMessageContaining("Expected: visible");
    }
  }

  @Test
  void wait_while_visible() {
    SelenideElement element = $(".detective").shouldBe(visible);

    try {
      element.waitWhile(visible, 42);
      fail("Expected ElementNotFound");
    }
    catch (ElementShouldNot expected) {
      assertThat(expected).hasMessageStartingWith("Element should not visible {.detective}"); // ugly text
      assertThat(expected).hasMessageContaining("Actual value: visible:true");
    }
  }

  @Test
  void wait_while_has_text() {
    SelenideElement element = $(".detective").shouldBe(visible);

    try {
      element.waitWhile(have(text("Miller")), 42);
      fail("Expected ElementNotFound");
    }
    catch (ElementShouldNot expected) {
      assertThat(expected).hasMessageStartingWith("Element should not have text 'Miller' {.detective}");
    }
  }
}
