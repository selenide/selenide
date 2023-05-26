package integration.collections;

import com.codeborne.selenide.ex.TextsMismatch;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.exactTextsCaseSensitiveInAnyOrder;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ElementsCollectionTextsTest extends ITest {

  @BeforeEach
  void setUp() {
    openFile("page_with_list_of_elements.html");
  }

  @Test
  void shouldMatchTextsOfElementsWithSameOrder() {
    List<String> expectedTexts = Arrays.asList("One", "Two", "Three");
    $$(".element").shouldHave(exactTextsCaseSensitiveInAnyOrder(expectedTexts));
  }

  @Test
  void shouldMatchTextsOfElementsWithDifferentOrder() {
    List<String> expectedTexts = Arrays.asList("One", "Three", "Two");
    $$(".element").shouldHave(exactTextsCaseSensitiveInAnyOrder(expectedTexts));
  }

  @Test
  void shouldFailWithExpectedTextsWithDifferentCase() {
    List<String> expectedTexts = Arrays.asList("one", "Three", "Two");
    assertThatThrownBy(() ->
      $$(".element").shouldHave(exactTextsCaseSensitiveInAnyOrder(expectedTexts)))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageStartingWith("Texts mismatch")
      .hasMessageContaining("Actual: [One, Two, Three]")
      .hasMessageContaining("Expected: [one, Three, Two]");
  }
}
