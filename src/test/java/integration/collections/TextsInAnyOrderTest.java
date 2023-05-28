package integration.collections;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.ex.TextsSizeMismatch;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TextsInAnyOrderTest extends ITest {
  @BeforeEach
  void openPage() {
    openFile("page_with_list_of_elements.html");
  }

  @Test
  void checksTexts() {
    $$(".element").shouldHave(textsInAnyOrder("One", "Two", "Three"));
    $$(".element").shouldHave(textsInAnyOrder(asList("One", "Two", "Three")));
  }

  @Test
  void ignoresOrderOfTexts() {
    $$(".element").shouldHave(textsInAnyOrder("Two", "One", "Three"));
    $$(".element").shouldHave(textsInAnyOrder("Three", "One", "Two"));
    $$(".element").shouldHave(textsInAnyOrder("Three", "Two", "One"));
  }

  @Test
  void ignoresWhitespacesInTexts() {
    $$("ol.spaces li").shouldHave(
      textsInAnyOrder("The \t\n first\n\r", "    The second      ", "The     third")
    );
  }

  @Test
  void acceptSubstrings() {
    $$(".element").shouldHave(textsInAnyOrder("On", "wo", "hre"));
  }

  @Test
  void errorMessage() {
    assertThatThrownBy(() -> $$(".element").shouldHave(textsInAnyOrder("One", "Two", "Four")))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith("Texts mismatch")
      .hasMessageContaining("Actual: [One, Two, Three]")
      .hasMessageContaining("Expected: [One, Two, Four]")
      .hasMessageContaining("Collection: .element");
  }

  @Test
  void checksElementsCount() {
    assertThatThrownBy(() -> $$(".element").shouldHave(textsInAnyOrder("One", "Two", "Three", "Four")))
      .isInstanceOf(TextsSizeMismatch.class)
      .hasMessageStartingWith("Texts size mismatch")
      .hasMessageContaining("Actual: [One, Two, Three], List size: 3")
      .hasMessageContaining("Expected: [One, Two, Three, Four], List size: 4")
      .hasMessageContaining("Collection: .element");
  }

  @Test
  void throwsElementNotFound() {
    assertThatThrownBy(() -> $$(".non-existing-elements").shouldHave(textsInAnyOrder("content1", "content2")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {.non-existing-elements}");
  }

  @Test
  void emptyArrayIsNotAllowed() {
    assertThatThrownBy(() -> $$(".element").shouldHave(textsInAnyOrder()))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("No expected texts given");
  }

  @Test
  void emptyListIsNotAllowed() {
    assertThatThrownBy(() -> $$(".element").shouldHave(textsInAnyOrder(emptyList())))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("No expected texts given");
  }
}
