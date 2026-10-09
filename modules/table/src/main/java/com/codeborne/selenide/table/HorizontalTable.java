package com.codeborne.selenide.table;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;

import static com.codeborne.selenide.Condition.match;
import static org.openqa.selenium.By.xpath;

/**
 * Lazy view over a table where every row is a header {@code <th>} followed by a value {@code <td>}.
 * Only the table's own rows ({@code ./tr}, {@code ./tbody/tr}, {@code ./thead/tr}, {@code ./tfoot/tr}) are read,
 * so labels of a table nested in a value cell are ignored.
 *
 * @since 7.19.0
 */
public final class HorizontalTable {
  private static final String OWN_ROWS = "./tr | ./tbody/tr | ./thead/tr | ./tfoot/tr";

  private final SelenideElement root;

  private HorizontalTable(SelenideElement root) {
    this.root = root;
  }

  /**
   * @param root the table element
   * @since 7.19.0
   */
  public static HorizontalTable of(SelenideElement root) {
    return new HorizontalTable(root);
  }

  /**
   * @return header cells of the table's own rows (labels of nested tables are ignored)
   * @since 7.19.0
   */
  public ElementsCollection headers() {
    return root.$$x("(" + OWN_ROWS + ")/th");
  }

  /**
   * @param header exact (trimmed) header text
   * @return the value cell of the row with the given header
   * @since 7.19.0
   */
  public SelenideElement value(String header) {
    WebElementCondition hasHeader = match("header = \"" + header + "\"",
      tr -> tr.findElements(xpath("./th")).stream().anyMatch(th -> th.getText().trim().equals(header)));
    return root.$$x(OWN_ROWS).findBy(hasHeader).$x("./td");
  }
}
