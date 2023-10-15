package integration.collections;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ExactTextsTest extends ITest {
  @BeforeEach
  void openPage() {
    openFile("page_with_list_of_elements.html");
  }

  @Test
  void checksTexts() {
    $$(".element").shouldHave(exactTexts("One", "Two", "Three"));
    $$(".element").shouldHave(exactTexts(asList("One", "Two", "Three")));
  }

  @Test
  void ignoresWhitespacesInTexts() {
    $$("ol.spaces li").shouldHave(
      exactTexts("The \t\n first\n\r", "    The second      ", "The     third")
    );
  }

  @Test
  void doesNotAcceptSubstrings() {
    assertThatThrownBy(() -> $$(".element").shouldHave(exactTexts("On", "Tw", "Thre")))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith("Text #0 mismatch (expected: \"On\", actual: \"One\")")
      .hasMessageContaining("Actual (3): [One, Two, Three]")
      .hasMessageContaining("Expected (3): [On, Tw, Thre]")
      .hasMessageContaining("Collection: .element");
  }

  @Test
  void checksElementsCount() {
    assertThatThrownBy(() -> $$(".element").shouldHave(exactTexts("One", "Two", "Three", "Four")))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith("List size mismatch (expected: 4, actual: 3)")
      .hasMessageContaining("Actual (3): [One, Two, Three]")
      .hasMessageContaining("Expected (4): [One, Two, Three, Four]");
  }

  @Test
  void throwsElementNotFound() {
    assertThatThrownBy(() -> $$(".non-existing-elements").shouldHave(exactTexts("content1", "content2")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {.non-existing-elements}");
  }

  @Test
  void throwsElementNotFound_causedBy_NoSuchElementException() {
    ElementsCollection collection = $$("not-existing-locator").first().$$("h1");

    assertThatThrownBy(() -> collection.shouldHave(exactTexts("One", "Two")))
      .isInstanceOf(ElementNotFound.class)
      .hasCauseExactlyInstanceOf(NoSuchElementException.class);
  }

  @Test
  void shouldThrow_ElementNotFound_causedBy_IndexOutOfBoundsException() {
    ElementsCollection collection = $$("not-existing-locator").get(1).$$("h1");

    assertThatThrownBy(() -> collection.shouldHave(exactTexts("One", "Two")))
      .isInstanceOf(ElementNotFound.class)
      .hasCauseExactlyInstanceOf(IndexOutOfBoundsException.class);
  }

  @Test
  void emptyArrayIsNotAllowed() {
    assertThatThrownBy(() -> $$(".element").shouldHave(exactTexts()))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("No expected texts given");
  }

  @Test
  void emptyListIsNotAllowed() {
    assertThatThrownBy(() -> $$(".element").shouldHave(exactTexts(emptyList())))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("No expected texts given");
  }

  @Test
  void textsInsideSvg() {
    openFile("page_with_svg.html");
    $$("#banana svg tspan").shouldHave(exactTexts("noT", "APPle", "THE frUIt"));
    $$("#banana svg text").shouldHave(exactTexts("You are not a BANANA!", "You are not an APPLE;", "You are the FRUIT."));
  }
}
