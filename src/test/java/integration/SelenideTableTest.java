package integration;

import com.codeborne.selenide.SelenideTable;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.SelenideTable.of;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SelenideTableTest extends ITest {
  private SelenideTable orders;

  @BeforeEach
  void openTable() {
    openFile("table_navigation.html");
    orders = of($("#orders"));
  }

  @Test
  void readsCellByZeroBasedBodyRowAndCellIndexes() {
    orders.cell(1, 1).shouldHave(exactText("Pending"));
  }

  @Test
  void waitsForDelayedRowsAndSurvivesTableRemount() {
    driver().executeJavaScript("window.delaySecondRow()");
    orders.cell(1, 1).shouldHave(exactText("Pending"), Duration.ofSeconds(2));

    driver().executeJavaScript("window.replaceOrders()");
    orders.cell(0, 0).shouldHave(exactText("1001"));
  }

  @Test
  void keepsNestedTableRowsOutOfOuterTableIndexes() {
    openFile("table_navigation_adversarial.html");
    of($("#nested-orders")).cell(1, 0).shouldHave(exactText("outer-2"));
  }

  @Test
  void keepsCapturedCellLazyAcrossTableRemount() {
    SelenideElement cell = orders.cell(0, 0);

    driver().executeJavaScript("window.replaceOrders()");

    cell.shouldHave(exactText("1001"));
  }

  @Test
  void reportsEmptyBodyWithNativeElementDiagnostics() {
    openFile("table_navigation_adversarial.html");
    assertThatThrownBy(() -> of($("#empty-orders")).cell(0, 0).should(exist))
      .hasMessageContaining("./tbody/tr");
  }

  @Test
  void reportsOutOfRangeRowWithNativeElementDiagnostics() {
    assertThatThrownBy(() -> orders.cell(2, 0).should(exist))
      .isInstanceOf(IndexOutOfBoundsException.class)
      .hasMessageContaining("Index 2 out of bounds");
  }

  @Test
  void reportsOutOfRangeCellWithNativeElementDiagnostics() {
    assertThatThrownBy(() -> orders.cell(0, 2).should(exist))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("./td | ./th");
  }
}
