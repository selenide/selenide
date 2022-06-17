package integration;

import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.pseudo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class PseudoTest extends ITest {

  @BeforeEach
  void openTestPage() {
    openFile("page_with_pseudo_elements.html");
  }

  @Test
  void shouldHavePseudo() {
    $("h1").shouldHave(pseudo(":first-letter", "color", "rgb(255, 0, 0)"));
    $("h2").shouldNotHave(pseudo(":first-letter", "color", "rgb(255, 0, 0)"));
    $("abbr").shouldHave(pseudo(":before", "content", "\"beforeContent\""));
    $("p").shouldNotHave(pseudo(":before", "content", "\"beforeContent\""));
    $("abbr").shouldHave(pseudo(":before", "\"beforeContent\""));
    $("p").shouldNotHave(pseudo(":before", "\"beforeContent\""));
  }

  @Test
  void getPseudo() {
    assertThat($("h1").pseudo(":first-letter", "color")).isEqualTo("rgb(255, 0, 0)");
    assertThat($("abbr").pseudo(":before", "content")).isEqualTo("\"beforeContent\"");
    assertThat($("p").pseudo(":after", "content")).isEqualTo("none");
    assertThat($("p").pseudo(":after", "")).isEqualTo("");
    assertThat($("abbr").pseudo(":before")).isEqualTo("\"beforeContent\"");
    assertThat($("p").pseudo(":after")).isEqualTo("none");
  }

  @Test
  void actualValue() {
    assertThatThrownBy(() ->
      $("abbr").shouldHave(pseudo(":before", "content", "\"nope\"")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have pseudo-element :before {content: \"nope\";} {abbr}")
      .hasMessageContaining("Element: '<abbr title=\"World Wide Web\">WWW</abbr>'")
      .hasMessageContaining("Actual value: :before {content: \"beforeContent\";}");
  }
}
