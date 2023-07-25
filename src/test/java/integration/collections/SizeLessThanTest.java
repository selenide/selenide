package integration.collections;

import com.codeborne.selenide.ex.ListSizeMismatch;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.sizeLessThan;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SizeLessThanTest extends ITest {

  @BeforeEach
  void openPage() {
    openFile("page_with_selects_without_jquery.html");
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
      .hasMessageStartingWith("List size mismatch")
      .hasMessageContaining("expected: < 4, actual: 4")
      .hasMessageContaining("collection: #radioButtons input");
  }
}
