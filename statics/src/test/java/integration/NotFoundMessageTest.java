package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ListSizeMismatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class NotFoundMessageTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void collectionFindBy() {
    assertThatThrownBy(
      () -> $$("#task-list>li").findBy(exactText("a")).doubleClick())
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#task-list>li.findBy(exact text 'a')}");
  }

  @Test
  void collectionFindByIndex() {
    assertThatThrownBy(
      () -> $$("#task-list>li").get(22).doubleClick())
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#task-list>li[22]}");
  }

  @Test
  void nthsElement() {
    assertThatThrownBy(
      () -> $("#task-list>li", 33).doubleClick())
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#task-list>li[33]}");
  }

  @Test
  void lastCollectionElement() {
    assertThatThrownBy(
      () -> $$("#task-list>li").last().doubleClick())
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#task-list>li:last}");
  }

  @Test
  void tailOfCollectionElement() {
    assertThatThrownBy(
      () -> $$("#task-list>li").last(55).shouldHave(size(55)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch: expected: = 55, actual: 0, collection: #task-list>li:last(55)");
  }

  @Test
  void firstElementOfCollectionElement() {
    assertThatThrownBy(
      () -> $$("#task-list>li").first().doubleClick())
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#task-list>li[0]}");
  }

  @Test
  void headOfCollectionElement() {
    assertThatThrownBy(
      () -> $$("#task-list>li").first(11).shouldHave(size(11)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch: expected: = 11, actual: 0, collection: #task-list>li.first(11)");
  }
}
