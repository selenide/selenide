package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.Condition.have;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    assertThatThrownBy(() -> element.should(have(text("Müller")), Duration.ofMillis(42)))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have text \"Müller\" {.detective}");
  }

  @Test
  void wait_until_visible() {
    SelenideElement element = $$("ul .nonexistent").get(1);

    assertThatThrownBy(() -> element.shouldBe(visible, Duration.ofMillis(42)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul .nonexistent[1]}")
      .hasMessageContaining("Expected: visible");
  }

  @Test
  void wait_while_visible() {
    SelenideElement element = $(".detective").shouldBe(visible);

    assertThatThrownBy(() -> element.shouldNot(visible, Duration.ofMillis(42)))
      .isInstanceOf(ElementShouldNot.class)
      .hasMessageStartingWith("Element should not visible {.detective}")
      .hasMessageContaining("Actual value: visible");
  }

  @Test
  void wait_while_has_text() {
    SelenideElement element = $(".detective").shouldBe(visible);

    assertThatThrownBy(() -> element.shouldNot(have(text("Miller detective")), Duration.ofMillis(42)))
      .isInstanceOf(ElementShouldNot.class)
      .hasMessageStartingWith("Element should not have text \"Miller detective\" {.detective}");
  }
}
