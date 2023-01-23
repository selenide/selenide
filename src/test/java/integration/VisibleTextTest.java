package integration;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class VisibleTextTest extends ITest {

  @BeforeEach
  public void prepare() {
    openFile("page_with_partial_visible_text.html");
  }

  @Test
  void shouldMatchExpectedVisibleTextWithSameCase() {
    withLongTimeout(() -> $("[name='field_value']").should(Condition.visibleText("987 65423 2Aa21")));
  }

  @Test
  void shouldMatchExpectedVisibleTextWithDifferentCase() {
    withLongTimeout(() -> $("[name='field_value']").should(Condition.visibleText("987 65423 2aa21")));
  }

  @Test
  void shouldMatchExpectedVisibleTextCaseSensitiveWithSameCase() {
    withLongTimeout(() -> $("[name='field_value']").should(Condition.visibleTextCaseSensitive("987 65423 2Aa21")));
  }
}
