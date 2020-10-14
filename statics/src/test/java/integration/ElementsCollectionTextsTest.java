package integration;

import com.codeborne.selenide.ex.TextsMismatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.exactTextsCaseSensitiveInAnyOrder;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ElementsCollectionTextsTest extends IntegrationTest {

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
    assertThrows(TextsMismatch.class, () ->
      $$(".element").shouldHave(exactTextsCaseSensitiveInAnyOrder(expectedTexts)));
  }
}
