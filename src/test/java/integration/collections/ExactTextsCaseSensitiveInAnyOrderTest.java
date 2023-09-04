package integration.collections;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.CollectionCondition.exactTextsCaseSensitiveInAnyOrder;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExactTextsCaseSensitiveInAnyOrderTest extends ITest {

  @BeforeEach
  void setUp() {
    openFile("page_with_list_of_elements.html");
  }

  @Test
  void shouldMatchTextsOfElementsWithSameOrder() {
    $$(".element").shouldHave(exactTextsCaseSensitiveInAnyOrder("One", "Two", "Three"));
    $$(".element").shouldHave(exactTextsCaseSensitiveInAnyOrder(asList("One", "Two", "Three")));
  }

  @Test
  void shouldMatchTextsOfElementsWithDifferentOrder() {
    $$(".element").shouldHave(exactTextsCaseSensitiveInAnyOrder("Two", "One", "Three"));
    $$(".element").shouldHave(exactTextsCaseSensitiveInAnyOrder("Two", "Three", "One"));
    $$(".element").shouldHave(exactTextsCaseSensitiveInAnyOrder("Three", "Two", "One"));
  }

  @ParameterizedTest
  @MethodSource("expectedTextsInWrongCase")
  void shouldNotMatchWithSameOrderAndDifferentCase(List<String> expectedTexts) {
    assertThatThrownBy(() -> $$(".element").shouldHave(exactTextsCaseSensitiveInAnyOrder(expectedTexts)))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith(String.format("Text #0 not found: \"%s\"", expectedTexts.get(0)))
      .hasMessageContaining("Actual (3): [One, Two, Three]")
      .hasMessageContaining("Expected (3): " + expectedTexts)
      .hasMessageContaining("Collection: .element");
  }

  private static Stream<List<String>> expectedTextsInWrongCase() {
    return Stream.of(
      asList("one", "Two", "Three"),
      asList("one", "two", "three"),
      asList("ONE", "TWO", "THREE")
    );
  }

  @Test
  void checksElementsCount() {
    assertThatThrownBy(() -> $$(".element").shouldHave(exactTextsCaseSensitiveInAnyOrder("One", "Two", "Three", "Four")))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith("List size mismatch (expected: 4, actual: 3)")
      .hasMessageContaining("Actual (3): [One, Two, Three]")
      .hasMessageContaining("Expected (4): [One, Two, Three, Four]")
      .hasMessageContaining("Collection: .element");
  }

  @Test
  void throwsElementNotFound() {
    assertThatThrownBy(() -> $$(".non-existing-elements").shouldHave(exactTextsCaseSensitiveInAnyOrder("content1", "content2")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {.non-existing-elements}")
      .hasMessageContaining("Expected: Exact texts case sensitive in any order [content1, content2]")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 1 ms.");
  }

  @Test
  void emptyArrayIsNotAllowed() {
    assertThatThrownBy(() -> $$(".element").shouldHave(exactTextsCaseSensitiveInAnyOrder()))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("No expected texts given");
  }

  @Test
  void emptyListIsNotAllowed() {
    assertThatThrownBy(() -> $$(".element").shouldHave(exactTextsCaseSensitiveInAnyOrder(emptyList())))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("No expected texts given");
  }

  @Test
  void textsInsideSvg() {
    openFile("page_with_svg.html");
    $$("#banana svg tspan").shouldHave(exactTextsCaseSensitiveInAnyOrder("the Fruit", "apple", "not"));
    $$("#banana svg text").shouldHave(exactTextsCaseSensitiveInAnyOrder(
      "You are not a banana!", "You are the Fruit.", "You are not an apple;"));
  }
}
