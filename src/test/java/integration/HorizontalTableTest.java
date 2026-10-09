package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.table.HorizontalTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class HorizontalTableTest extends ITest {
  private static final Duration TIMEOUT = Duration.ofSeconds(5);
  private HorizontalTable table;

  @BeforeEach
  void openPage() {
    openFile("table_helper.html");
    table = HorizontalTable.of($("#horizontal-customers"));
  }

  @Test
  void readsValueByHeader() {
    table.value("Name").shouldHave(exactText("Bill Gates"));
  }

  @Test
  void waitsForDelayedHeader() {
    table.value("Telephone 2").shouldHave(exactText("555 77 855"), TIMEOUT);
  }

  @Test
  void survivesRemount() {
    table.value("Telephone 2").shouldHave(exactText("555 77 855"), TIMEOUT);
    driver().executeJavaScript("window.reloadHorizontalTable()");
    table.value("Telephone 2").shouldHave(exactText("555 77 856"), TIMEOUT);
  }

  @Test
  void missingHeaderFails() {
    assertThatThrownBy(() -> table.value("Missing Header").should(exist, Duration.ofMillis(600)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Missing Header");
  }

  @Test
  void doesNotMatchHeaderPrefix() {
    table.value("Telephone").should(not(exist));
  }
}
