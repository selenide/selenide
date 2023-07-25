package integration.collections;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ListSizeMismatch;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeNotEqual;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SizeNotEqualTest extends ITest {

  @BeforeEach
  void openPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void size_notEqual() {
    $$("#radioButtons input").shouldHave(size(4));
    $$("#radioButtons input").shouldHave(sizeNotEqual(3));
    $$("#radioButtons input").shouldHave(sizeNotEqual(5));
  }

  @Test
  void size_notEqual_failure() {
    assertThatThrownBy(() -> $$("#radioButtons input").shouldHave(sizeNotEqual(4)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch")
      .hasMessageContaining("expected: <> 4, actual: 4")
      .hasMessageContaining("collection: #radioButtons input");
  }

  @Test
  void emptyCollection() {
    $$("not-existing-locator").shouldHave(sizeNotEqual(3));
  }

  @Test
  void emptyFilteredCollection() {
    ElementsCollection collection = $$("not-existing-locator").first().$$("#multirowTable");

    assertThatThrownBy(() -> collection.shouldHave(sizeNotEqual(0)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch")
      .hasMessageContaining("expected: <> 0")
      .hasMessageContaining("actual: 0")
      .hasMessageContaining("collection: not-existing-locator[0]/#multirowTable")
      .hasCauseExactlyInstanceOf(NoSuchElementException.class);
  }

  @Test
  void emptyCollectionByIndex() {
    ElementsCollection collection = $$("not-existing-locator").get(1).$$("#multirowTable");

    assertThatThrownBy(() -> collection.shouldHave(sizeNotEqual(0)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {not-existing-locator[1]/#multirowTable}")
      .hasCauseExactlyInstanceOf(IndexOutOfBoundsException.class);
  }
}
