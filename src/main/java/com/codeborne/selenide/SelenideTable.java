package com.codeborne.selenide;

import org.openqa.selenium.By;

/**
 * Lazy navigation over cells of an ordinary HTML table.
 *
 * <pre>{@code
 * SelenideTable.of($("#orders")).cell(1, 2).shouldHave(exactText("Pending"));
 * }</pre>
 *
 * <p>Rows are direct {@code tbody > tr} children, cells are direct {@code td}/{@code th} children of a row,
 * so rows of nested tables are not counted.</p>
 *
 * @since 7.19.0
 */
public final class SelenideTable {
  private static final By BODY_ROWS = By.xpath("./tbody/tr");
  private static final By ROW_CELLS = By.xpath("./td | ./th");

  private final SelenideElement table;

  private SelenideTable(SelenideElement table) {
    this.table = table;
  }

  public static SelenideTable of(SelenideElement table) {
    return new SelenideTable(table);
  }

  /**
   * @param rowIndex 0-based index of a body row
   * @param cellIndex 0-based index of a cell in the row
   * @return lazy cell element, resolved only when an action or a check is called
   */
  public SelenideElement cell(int rowIndex, int cellIndex) {
    return table.findAll(BODY_ROWS).get(rowIndex).findAll(ROW_CELLS).get(cellIndex);
  }
}
