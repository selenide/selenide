package integration.collections;

import com.codeborne.selenide.ex.ListSizeMismatch;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SizeGreaterThanOrEqualTest extends ITest {

  @BeforeEach
  void openPage() {
    openFile("page_with_selects_without_jquery.html");
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
      .hasMessageStartingWith("List size mismatch")
      .hasMessageContaining("expected: >= 5, actual: 4")
      .hasMessageContaining("collection: #radioButtons input")
      .hasMessageContaining("""
        Elements: [
        \t<input name="me" type="radio" value="master"></input>,
        \t<input name="me" type="radio" value="margarita"></input>,
        \t<input name="me" type="radio" value="cat"></input>,
        \t<input name="me" type="radio" value="woland"></input>
        ]""")
      .hasNoCause();
  }
}
