package integration.collections;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.ex.TextsSizeMismatch;
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
      .hasMessageContaining("Actual: [One, Two, Three]")
      .hasMessageContaining("Expected: [Two, One, Three]");
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
      .hasMessageStartingWith("Texts mismatch")
      .hasMessageContaining("Actual: [One, Two, Three]")
      .hasMessageContaining("Expected: [On, Two, Three]")
      .hasMessageContaining("Collection: .element");
  }

  @Test
  void doesNotAcceptInvalidCase() {
    assertThatThrownBy(() -> $$(".element").shouldHave(exactTextsCaseSensitive("One", "two", "Three")))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageContaining("Actual: [One, Two, Three]")
      .hasMessageContaining("Expected: [One, two, Three]");
  }

  @Test
  void checksElementsCount() {
    assertThatThrownBy(() -> $$(".element").shouldHave(exactTextsCaseSensitive("One", "Two", "Three", "Four")))
      .isInstanceOf(TextsSizeMismatch.class)
      .hasMessageStartingWith("Texts size mismatch")
      .hasMessageContaining("Actual: [One, Two, Three], List size: 3")
      .hasMessageContaining("Expected: [One, Two, Three, Four], List size: 4")
      .hasMessageContaining("Collection: .element");
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
}
