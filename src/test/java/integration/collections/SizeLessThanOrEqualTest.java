package integration.collections;

import com.codeborne.selenide.ex.ListSizeMismatch;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.sizeLessThanOrEqual;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SizeLessThanOrEqualTest extends ITest {

  @BeforeEach
  void openPage() {
    openFile("page_with_selects_without_jquery.html");
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
      .hasMessageStartingWith("List size mismatch")
      .hasMessageContaining("collection: #radioButtons input")
      .hasMessageContaining("expected: <= 3, actual: 4");
  }
}
