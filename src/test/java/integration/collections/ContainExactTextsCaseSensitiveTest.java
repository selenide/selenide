package integration.collections;

import com.codeborne.selenide.ex.DoesNotContainTextsError;
import com.codeborne.selenide.ex.ElementNotFound;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.containExactTextsCaseSensitive;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ContainExactTextsCaseSensitiveTest extends ITest {
  @BeforeEach
  void openPage() {
    openFile("page_with_list_of_elements.html");
  }

  @Test
  void checksTexts() {
    $$(".element").should(containExactTextsCaseSensitive("One", "Two", "Three"));
    $$(".element").should(containExactTextsCaseSensitive(asList("One", "Two", "Three")));
  }

  @Test
  void collectionUnordered() {
    $$(".element").should(containExactTextsCaseSensitive("Two", "One", "Three"));
    $$(".element").should(containExactTextsCaseSensitive("Two", "Three", "One"));
    $$(".element").should(containExactTextsCaseSensitive("Three", "Two", "One"));
  }

  @Test
  void collectionUnorderedMoreElements() {
    $$(".element").should(containExactTextsCaseSensitive("Two", "One"));
    $$(".element").should(containExactTextsCaseSensitive("Two", "Three"));
    $$(".element").should(containExactTextsCaseSensitive("One", "Two"));
    $$(".element").should(containExactTextsCaseSensitive("Three", "Two"));
    $$(".element").should(containExactTextsCaseSensitive("One"));
  }

  @Test
  void doesNotAcceptSubstrings() {
    assertThatThrownBy(() -> $$(".element").should(containExactTextsCaseSensitive("On", "Tw", "Thre")))
      .isInstanceOf(DoesNotContainTextsError.class)
      .hasMessageStartingWith("""
        The collection with text elements: [One, Two, Three]
        should contain all of the following text elements: [On, Tw, Thre]
        but could not find these elements: [On, Tw, Thre]""")
      .hasMessageContaining("Collection: .element");
  }

  @Test
  void doesNotAcceptWrongCase() {
    assertThatThrownBy(() -> $$(".element").should(containExactTextsCaseSensitive("one", "Two", "Three")))
      .isInstanceOf(DoesNotContainTextsError.class)
      .hasMessageStartingWith("The collection with text elements: [One, Two, Three]")
      .hasMessageContaining("should contain all of the following text elements: [one, Two, Three]")
      .hasMessageContaining("but could not find these elements: [one]");
  }

  @Test
  void checksElementsCount() {
    assertThatThrownBy(() -> $$(".element").should(containExactTextsCaseSensitive("One", "Two", "Three", "Four")))
      .isInstanceOf(DoesNotContainTextsError.class)
      .hasMessageStartingWith("The collection with text elements: [One, Two, Three]")
      .hasMessageContaining("should contain all of the following text elements: [One, Two, Three, Four]")
      .hasMessageContaining("but could not find these elements: [Four]");
  }

  @Test
  void throwsElementNotFound() {
    assertThatThrownBy(() -> $$(".non-existing-elements").should(containExactTextsCaseSensitive("content1", "content2")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {.non-existing-elements}");
  }

  @Test
  void emptyArrayIsNotAllowed() {
    assertThatThrownBy(() -> $$(".element").should(containExactTextsCaseSensitive()))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("No expected texts given");
  }

  @Test
  void emptyListIsNotAllowed() {
    assertThatThrownBy(() -> $$(".element").should(containExactTextsCaseSensitive(emptyList())))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("No expected texts given");
  }

  @Test
  void textsInsideSvg() {
    openFile("page_with_svg.html");
    $$("#banana svg tspan").should(containExactTextsCaseSensitive("not", "the Fruit"));
    $$("#banana svg text").should(containExactTextsCaseSensitive(
      "You are not a banana!", "You are not an apple;"));
  }
}
