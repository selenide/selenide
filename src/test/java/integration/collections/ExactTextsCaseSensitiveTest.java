package integration.collections;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.exactTextsCaseSensitive;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ExactTextsCaseSensitiveTest extends ITest {
  @BeforeEach
  void openPage() {
    openFile("page_with_list_of_elements.html");
  }

  @Test
  void checksTexts() {
    $$(".element").shouldHave(exactTextsCaseSensitive("One", "Two", "Three"));
    $$(".element").shouldHave(exactTextsCaseSensitive(asList("One", "Two", "Three")));
  }

  @Test
  void checksOrderOfTexts() {
    assertThatThrownBy(() -> $$(".element").shouldHave(exactTextsCaseSensitive("Two", "One", "Three")))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageContaining("Actual (3): [One, Two, Three]")
      .hasMessageContaining("Expected (3): [Two, One, Three]");
  }

  @Test
  void ignoresWhitespacesInTexts() {
    $$("ol.spaces li").shouldHave(
      exactTextsCaseSensitive("The \t\n first\n\r", "    The second      ", "The     third")
    );
  }

  @Test
  void doesNotAcceptSubstrings() {
    assertThatThrownBy(() -> $$(".element").shouldHave(exactTextsCaseSensitive("On", "Two", "Three")))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith("Text #0 mismatch (expected: \"On\", actual: \"One\")")
      .hasMessageContaining("Actual (3): [One, Two, Three]")
      .hasMessageContaining("Expected (3): [On, Two, Three]")
      .hasMessageContaining("Collection: .element");
  }

  @Test
  void doesNotAcceptInvalidCase() {
    assertThatThrownBy(() -> $$(".element").shouldHave(exactTextsCaseSensitive("One", "two", "Three")))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageContaining("Actual (3): [One, Two, Three]")
      .hasMessageContaining("Expected (3): [One, two, Three]");
  }

  @Test
  void checksElementsCount() {
    assertThatThrownBy(() -> $$(".element").shouldHave(exactTextsCaseSensitive("One", "Two", "Three", "Four")))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith("List size mismatch (expected: 4, actual: 3)")
      .hasMessageContaining("Actual (3): [One, Two, Three]")
      .hasMessageContaining("Expected (4): [One, Two, Three, Four]");
  }

  @Test
  void throwsElementNotFound() {
    assertThatThrownBy(() -> $$(".non-existing-elements").shouldHave(exactTextsCaseSensitive("content1", "content2")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {.non-existing-elements}");
  }

  @Test
  void emptyArrayIsNotAllowed() {
    assertThatThrownBy(() -> $$(".element").shouldHave(exactTextsCaseSensitive()))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("No expected texts given");
  }

  @Test
  void emptyListIsNotAllowed() {
    assertThatThrownBy(() -> $$(".element").shouldHave(exactTextsCaseSensitive(emptyList())))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("No expected texts given");
  }

  @Test
  void textsInsideSvg() {
    openFile("page_with_svg.html");
    $$("#banana svg tspan").shouldHave(exactTextsCaseSensitive("not", "apple", "the Fruit"));
    $$("#banana svg text").shouldHave(exactTextsCaseSensitive("You are not a banana!", "You are not an apple;", "You are the Fruit."));
  }
}
