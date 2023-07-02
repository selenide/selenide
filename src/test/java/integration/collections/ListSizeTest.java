package integration.collections;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ListSizeMismatch;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.CollectionCondition.size;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ListSizeTest extends ITest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void size_equals() {
    $$("#radioButtons input").shouldHave(size(4));
    $$("h1").shouldHave(size(2));
    $$(".missing").shouldHave(size(0));
  }

  @Test
  void shouldHaveZeroSizeWhenFindCollectionInLastElementOfEmptyCollection() {
    $$("#not_exist").last().$$("#multirowTable").shouldHave(size(0));
  }

  @Test
  void shouldHaveZeroSizeWhenFindCollectionInLastElementOfFullCollection() {
    $$("#user-table td").last().$$("#not_exist").shouldHave(size(0));
  }

  @Test
  void errorMessage() {
    assertThatThrownBy(() -> $$("#radioButtons input").shouldHave(size(3)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch: expected: = 3, actual: 4, collection: #radioButtons input")
      .hasNoCause();
  }

  @Test
  void errorMessageCausedByMissingParent() {
    ElementsCollection elementsCollection = $$("not-existing-locator").first().$$("#multirowTable");

    assertThatThrownBy(() -> elementsCollection.shouldHave(size(1)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasCauseExactlyInstanceOf(NoSuchElementException.class);
  }

  @Test
  void errorMessageCausedByMissingParentByIndex() {
    ElementsCollection elementsCollection = $$("not-existing-locator").get(1).$$("#multirowTable");

    assertThatThrownBy(() -> elementsCollection.shouldHave(size(1)))
      .isInstanceOf(ElementNotFound.class)
      .hasCauseExactlyInstanceOf(IndexOutOfBoundsException.class);
  }

  @Test
  void errorWhenFindCollectionInLastElementOfEmptyCollection() {
    assertThatThrownBy(() -> $$("#not_exist").last().$$("#multirowTable").shouldHave(size(1)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#not_exist:last/#multirowTable}")
      .hasCauseInstanceOf(IndexOutOfBoundsException.class);
  }
}
