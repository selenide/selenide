package integration.collections;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ListSizeMismatch;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SizeGreaterThanTest extends ITest {

  private final ElementsCollection inputs = $$("#radioButtons input");

  @BeforeEach
  void openPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void checks_that_collections_has_at_least_so_many_elements() {
    inputs.shouldHave(sizeGreaterThan(3));
    inputs.shouldHave(sizeGreaterThan(2));
    inputs.shouldHave(sizeGreaterThan(1));
    inputs.shouldHave(sizeGreaterThan(0));
    inputs.shouldHave(sizeGreaterThan(-1));
  }

  @Test
  void errorMessage() {
    assertThatThrownBy(() -> $$("h1").shouldHave(sizeGreaterThan(2)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch")
      .hasMessageContaining("expected: > 2, actual: 2")
      .hasMessageContaining("collection: h1")
      .hasNoCause();
  }

  @Test
  void emptyList() {
    ElementsCollection elementsCollection = $$(".missing");

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeGreaterThan(0)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch")
      .hasMessageContaining("expected: > 0, actual: 0")
      .hasMessageContaining("collection: .missing")
      .hasNoCause();
  }

  @Test
  void emptyFilteredCollection() {
    ElementsCollection elementsCollection = $$("not-existing-locator").first().$$("#multirowTable");

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeGreaterThan(0)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageContaining("collection: not-existing-locator[0]/#multirowTable")
      .hasCauseExactlyInstanceOf(NoSuchElementException.class);
  }

  @Test
  void emptyCollectionByIndex() {
    ElementsCollection elementsCollection = $$("not-existing-locator").get(1).$$("#multirowTable");

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeGreaterThan(0)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {not-existing-locator[1]/#multirowTable}")
      .hasCauseExactlyInstanceOf(IndexOutOfBoundsException.class);

  }
}
