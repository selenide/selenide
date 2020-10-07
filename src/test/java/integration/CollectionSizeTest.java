package integration;

import com.codeborne.selenide.ex.ListSizeMismatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.sizeLessThan;
import static com.codeborne.selenide.CollectionCondition.sizeLessThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.sizeNotEqual;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class CollectionSizeTest extends ITest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void size_equals() {
    $$("#radioButtons input").shouldHave(size(4));
  }

  @Test
  void size_greaterThan() {
    $$("#radioButtons input").shouldHave(sizeGreaterThan(3));
    $$("#radioButtons input").shouldHave(sizeGreaterThan(2));
    $$("#radioButtons input").shouldHave(sizeGreaterThan(1));
  }

  @Test
  void size_greaterThan_failure() {
    assertThatThrownBy(() -> $$("#radioButtons input").shouldHave(sizeGreaterThan(4)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageContaining("expected: > 4, actual: 4");
  }

  @Test
  void size_greaterThanOrEqual() {
    $$("#radioButtons input").shouldHave(sizeGreaterThanOrEqual(4));
    $$("#radioButtons input").shouldHave(sizeGreaterThanOrEqual(3));
  }

  @Test
  void size_greaterThanOrEqual_failure() {
    assertThatThrownBy(() -> $$("#radioButtons input").shouldHave(sizeGreaterThanOrEqual(5)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageContaining("expected: >= 5, actual: 4");
  }

  @Test
  void size_lessThan() {
    $$("#radioButtons input").shouldHave(sizeLessThan(5));
    $$("#radioButtons input").shouldHave(sizeLessThan(6));
    $$("#radioButtons input").shouldHave(sizeLessThan(7));
  }

  @Test
  void size_lessThan_failure() {
    assertThatThrownBy(() -> $$("#radioButtons input").shouldHave(sizeLessThan(4)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageContaining("expected: < 4, actual: 4");
  }

  @Test
  void size_lessThanOrEqual() {
    $$("#radioButtons input").shouldHave(sizeLessThanOrEqual(4));
    $$("#radioButtons input").shouldHave(sizeLessThanOrEqual(5));
  }

  @Test
  void size_lessThanOrEqual_failure() {
    assertThatThrownBy(() -> $$("#radioButtons input").shouldHave(sizeLessThanOrEqual(3)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageContaining("expected: <= 3, actual: 4");
  }

  @Test
  void size_notEqual() {
    $$("#radioButtons input").shouldHave(sizeNotEqual(3));
    $$("#radioButtons input").shouldHave(sizeNotEqual(5));
  }

  @Test
  void size_notEqual_failure() {
    assertThatThrownBy(() -> $$("#radioButtons input").shouldHave(sizeNotEqual(4)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageContaining("expected: <> 4, actual: 4");
  }
}
