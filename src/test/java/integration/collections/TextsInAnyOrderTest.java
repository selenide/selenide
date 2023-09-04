package integration.collections;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
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
      .hasMessageStartingWith("Text #2 not found: \"Four\"")
      .hasMessageContaining("Actual (3): [One, Two, Three]")
      .hasMessageContaining("Expected (3): [One, Two, Four]")
      .hasMessageContaining("Collection: .element");
  }

  @Test
  void checksElementsCount() {
    assertThatThrownBy(() -> $$(".element").shouldHave(textsInAnyOrder("One", "Two", "Three", "Four")))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith("List size mismatch (expected: 4, actual: 3)")
      .hasMessageContaining("Actual (3): [One, Two, Three]")
      .hasMessageContaining("Expected (4): [One, Two, Three, Four]")
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

  @Test
  void textsInsideSvg() {
    openFile("page_with_svg.html");
    $$("#banana svg tspan").shouldHave(textsInAnyOrder("not", "appl", "fruit"));
    $$("#banana svg text").shouldHave(textsInAnyOrder("the Fruit", "not a banana", "not an apple"));
  }
}
