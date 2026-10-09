package com.codeborne.selenide.table;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class TableColumnXpathTest {
  @Test
  void groupsRowsXpathSoUnionsKeepTheCellStep() {
    String step = Table.cellStep("./thead/tr | ./tbody/tr", "./td");

    assertThat(Table.columnXpath("./thead/tr | ./tbody/tr", step, 1)).isEqualTo("(./thead/tr | ./tbody/tr)/td[2]");
  }

  @Test
  void keepsHtmlLayoutCellPredicate() {
    String step = Table.cellStep("./tbody/tr[td]", "./*[self::td or self::th]");

    assertThat(Table.columnXpath("./tbody/tr[td]", step, 0)).isEqualTo("(./tbody/tr[td])/*[self::td or self::th][1]");
  }

  @Test
  void rejectsDescendantCellsXpath() {
    assertThatThrownBy(() -> Table.cellStep("./tbody/tr", ".//td"))
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessageContaining("column()");
  }

  @Test
  void rejectsNonXpathAndUnionCells() {
    assertThatThrownBy(() -> Table.cellStep(null, "./td")).isInstanceOf(UnsupportedOperationException.class);
    assertThatThrownBy(() -> Table.cellStep("./tbody/tr", null)).isInstanceOf(UnsupportedOperationException.class);
    assertThatThrownBy(() -> Table.cellStep("./tbody/tr", "./td | ./th")).isInstanceOf(UnsupportedOperationException.class);
  }
}
