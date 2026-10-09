package com.codeborne.selenide.table;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

/**
 * A row of a {@link Table}; cells are addressed by index or by displayed header text.
 *
 * @since 7.19.0
 */
public final class TableRow {
  private final SelenideElement self;
  private final Table table;

  TableRow(SelenideElement self, Table table) {
    this.self = self;
    this.table = table;
  }

  /**
   * @return the row element
   * @since 7.19.0
   */
  public SelenideElement self() {
    return self;
  }

  /**
   * @return all cells of this row
   * @since 7.19.0
   */
  public ElementsCollection cells() {
    return self.$$(table.layout().cells());
  }

  /**
   * @param index 0-based cell index
   * @return the cell at given index
   * @since 7.19.0
   */
  public SelenideElement cell(int index) {
    return cells().get(index);
  }

  /**
   * Waits until the header is displayed and returns the cell of this row in that column.
   *
   * @param header displayed header text (whitespace-normalized, case-sensitive)
   * @return the cell in the given column
   * @throws TableColumnException if the header is not displayed within timeout, or is displayed more than once
   * @since 7.19.0
   */
  public SelenideElement cell(String header) {
    return cells().get(table.columnIndex(header));
  }
}
