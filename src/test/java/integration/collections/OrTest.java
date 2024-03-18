package integration.collections;

import com.codeborne.selenide.ex.UIAssertionError;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.texts;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrTest extends ITest {
  @BeforeEach
  void openPage() {
    openFile("page_with_list_of_elements.html");
  }

  @Test
  void checksTexts() {
    $$(".element").shouldHave(texts("One", "Two", "Four").or(texts("One", "Two", "Three")));
  }

  @Test
  void errorMessage() {
    assertThatThrownBy(() -> $$(".element").shouldHave(texts("A", "B", "C").or(texts("1", "2", "3")), Duration.ofMillis(2)))
      .isInstanceOf(UIAssertionError.class)
      .hasMessageContaining("Actual: [One, Two, Three]")
      .hasMessageContaining("Expected: texts [A, B, C] OR texts [1, 2, 3]")
      .hasMessageContaining("Collection: .element")
      .hasMessageContaining("Screenshot:")
      .hasMessageContaining("Timeout: 2 ms.");
  }
}
