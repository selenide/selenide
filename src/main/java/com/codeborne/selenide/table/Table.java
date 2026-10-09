package com.codeborne.selenide.table;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.UIAssertionError;
import org.jspecify.annotations.Nullable;

import static com.codeborne.selenide.CollectionCondition.anyMatch;
import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static java.util.Objects.requireNonNull;
import static org.openqa.selenium.By.xpath;

/**
 * Thin header-aware view over a table-like element. Returned elements and collections are lazy:
 * {@code row(column, value)} and {@code rows(column, value)} re-resolve the column by displayed header text on every
 * evaluation, while {@code cell(header)} and {@code column(header)} resolve the column index once, when called
 * (call them again after columns are reordered).
 *
 * <pre>{@code
 * Table customers = Table.of($("#customers"), TableLayout.html());
 * customers.row("Company", "Ernst Handel").cell("Country").shouldHave(exactText("Austria"));
 * }</pre>
 *
 * @since 7.19.0
 */
public final class Table {
  private static final String UNSUPPORTED_COLUMN =
    "column() needs XPath row and single-step cell locators; use rows() and TableRow.cell()";

  private final SelenideElement root;
  private final TableLayout layout;

  private Table(SelenideElement root, TableLayout layout) {
    this.root = requireNonNull(root, "root");
    this.layout = requireNonNull(layout, "layout");
  }

  /**
   * @param root the table element
   * @param layout locators of rows, cells and headers
   * @since 7.19.0
   */
  public static Table of(SelenideElement root, TableLayout layout) {
    return new Table(root, layout);
  }

  /**
   * @return the table element
   * @since 7.19.0
   */
  public SelenideElement root() {
    return root;
  }

  /**
   * @return header cells
   * @since 7.19.0
   */
  public ElementsCollection headers() {
    return root.$$(layout.headers());
  }

  /**
   * @return data rows
   * @since 7.19.0
   */
  public ElementsCollection rows() {
    return root.$$(layout.rows());
  }

  /**
   * @param index 0-based row index
   * @return the row at given index
   * @since 7.19.0
   */
  public TableRow row(int index) {
    return new TableRow(rows().get(index), this);
  }

  /**
   * Finds the first row whose cell in the given column has exactly the given (whitespace-normalized) text.
   *
   * @param column displayed header text
   * @param value expected cell text
   * @return the first matching row
   * @since 7.19.0
   */
  public TableRow row(String column, String value) {
    return new TableRow(rows().findBy(new ColumnValueCondition(this, column, value)), this);
  }

  /**
   * Wraps a row element found by the caller, so its cells can be addressed by header. The element must be a row
   * of this table matching the layout's cells locator; it may be lazy.
   *
   * @param row the row element
   * @return the wrapped row
   * @since 7.19.0
   */
  public TableRow row(SelenideElement row) {
    return new TableRow(requireNonNull(row, "row"), this);
  }

  /**
   * @param column displayed header text
   * @param value expected cell text
   * @return all rows whose cell in the given column has exactly the given (whitespace-normalized) text
   * @since 7.19.0
   */
  public ElementsCollection rows(String column, String value) {
    return rows().filterBy(new ColumnValueCondition(this, column, value));
  }

  /**
   * Cells of the given column in every row. Supported only when the layout's rows locator is {@code By.xpath} and
   * its cells locator is a single {@code By.xpath} child step starting with {@code ./} (not {@code .//}, no
   * {@code |} unions), as in {@link TableLayout#html()} and {@link TableLayout#aria()}; other layouts throw
   * {@link UnsupportedOperationException}, use {@link #rows()} and {@link TableRow#cell(String)} instead.
   * The column index is resolved once, when this method is called.
   *
   * @param header displayed header text
   * @return cells of the given column
   * @since 7.19.0
   */
  public ElementsCollection column(String header) {
    String rowsXpath = TableLayout.xpath(layout.rows()).orElse(null);
    String cellStep = cellStep(rowsXpath, TableLayout.xpath(layout.cells()).orElse(null));
    return root.$$(xpath(columnXpath(rowsXpath, cellStep, columnIndex(header))));
  }

  static String cellStep(@Nullable String rowsXpath, @Nullable String cellsXpath) {
    if (rowsXpath == null || cellsXpath == null || !cellsXpath.startsWith("./") || cellsXpath.startsWith(".//")
      || cellsXpath.contains("|")) {
      throw new UnsupportedOperationException(UNSUPPORTED_COLUMN);
    }
    return cellsXpath.substring(2);
  }

  static String columnXpath(String rowsXpath, String cellStep, int index) {
    return "(" + rowsXpath + ")/" + cellStep + "[" + (index + 1) + "]";
  }

  TableLayout layout() {
    return layout;
  }

  int columnIndex(String header) {
    ElementsCollection headers = headers();
    String expected = ColumnResolver.normalize(header);
    try {
      headers.shouldHave(itemWithText(header).or(anyMatch("normalized header \"" + expected + "\"",
        th -> ColumnResolver.normalize(th.getText()).equals(expected))));
    }
    catch (UIAssertionError e) {
      throw new TableColumnException(header, "not found", root.toString(), ColumnResolver.normalize(headers.texts()));
    }
    return ColumnResolver.indexOf(headers.texts(), header, root.toString());
  }
}
