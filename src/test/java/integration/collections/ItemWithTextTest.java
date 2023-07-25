package integration.collections;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementWithTextNotFound;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ItemWithTextTest extends ITest {

  @BeforeEach
  void openPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void checks_that_some_of_elements_has_given_text() {
    $$("#user-table tbody tr td.firstname")
      .shouldHave(itemWithText("Bob"));
  }

  @Test
  void doesNotAcceptEmptyList() {
    ElementsCollection collection = $$(".missing");

    assertThatThrownBy(() -> collection.shouldHave(itemWithText("Bob")))
      .isInstanceOf(ElementWithTextNotFound.class)
      .hasMessageStartingWith("Element with text not found")
      .hasMessageContaining("Expected: [Bob]")
      .hasMessageContaining("Collection: .missing")
      .hasNoCause();
  }

  @Test
  void emptyFilteredCollection() {
    ElementsCollection collection = $$(".missing").first().$$("#multirowTable");

    assertThatThrownBy(() -> collection.shouldHave(itemWithText("Bob")))
      .isInstanceOf(ElementWithTextNotFound.class)
      .hasMessageStartingWith("Element with text not found")
      .hasMessageContaining("Expected: [Bob]")
      .hasMessageContaining("Collection: .missing")
      .hasCauseExactlyInstanceOf(NoSuchElementException.class);
  }

  @Test
  void emptyCollectionByIndex() {
    ElementsCollection collection = $$(".missing").get(1).$$("#multirowTable");
    assertThatThrownBy(() -> collection.shouldHave(itemWithText("Bob")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {.missing[1]/#multirowTable}")
      .hasCauseExactlyInstanceOf(IndexOutOfBoundsException.class);
  }

  @Test
  void errorMessage() {
    ElementsCollection elements = $$("#user-table tbody tr td.firstname");

    assertThatThrownBy(() -> elements.shouldHave(itemWithText("Luis")))
      .isInstanceOf(ElementWithTextNotFound.class)
      .hasMessageStartingWith("Element with text not found")
      .hasMessageContaining("Actual: [Bob, John]")
      .hasMessageContaining("Expected: [Luis]")
      .hasMessageContaining("Collection: #user-table tbody tr td.firstname");
  }
}
