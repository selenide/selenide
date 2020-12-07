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

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

final class ShouldMethodWithTimeoutFailsOnTest extends IntegrationTest {
  @BeforeEach
  void openPage() {
    givenHtml(
      "<ul>Hello to:",
      "<li class='the-expanse detective'>Miller <label>detective</label></li>",
      "<li class='the-expanse missing'>Julie Mao</li>",
      "<li class='the-expanse hidden' style='display: none;'>Julie Mao</li>",
      "</ul>"
    );
    Configuration.timeout = 1;
  }

  @Test
  void should_have_with_timeout() {
    SelenideElement element = $(".detective");

    try {
      element.shouldHave(text("Müller"), Duration.ofMillis(43));
      fail("Expected ElementNotFound");
    }
    catch (ElementShould expected) {
      assertThat(expected).hasMessageStartingWith("Element should have text 'Müller' {.detective}");
      assertThat(expected).hasMessageContaining("Timeout: 43 ms.");
    }
  }

  @Test
  void should_be_with_timeout() {
    SelenideElement element = $$("ul .nonexistent").get(1);

    try {
      element.shouldBe(visible, Duration.ofSeconds(0, 1_000_000));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected).hasMessageStartingWith("Element not found {ul .nonexistent[1]}");
      assertThat(expected).hasMessageContaining("Expected: visible");
      assertThat(expected).hasMessageContaining("Timeout: 1 ms.");
    }
  }

  @Test
  void should_not_be_with_timeout() {
    SelenideElement element = $(".detective").shouldBe(visible);

    try {
      element.shouldNotBe(visible, Duration.ofMillis(46));
      fail("Expected ElementNotFound");
    }
    catch (ElementShouldNot expected) {
      assertThat(expected).hasMessageStartingWith("Element should not be visible {.detective}");
      assertThat(expected).hasMessageContaining("Actual value: visible:true");
      assertThat(expected).hasMessageContaining("Timeout: 46 ms.");
    }
  }

  @Test
  void should_not_have_with_timeout() {
    SelenideElement element = $(".detective").shouldBe(visible);

    try {
      element.shouldNotHave(text("Miller"), Duration.ofNanos(20001000));
      fail("Expected ElementNotFound");
    }
    catch (ElementShouldNot expected) {
      assertThat(expected).hasMessageStartingWith("Element should not have text 'Miller' {.detective}");
      assertThat(expected).hasMessageContaining("Timeout: 20 ms.");
    }
  }
}
