package com.codeborne.selenide.table;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;

import static com.codeborne.selenide.Condition.match;
import static org.openqa.selenium.By.xpath;

/**
 * Lazy view over a table where every row is a header {@code <th>} followed by a value {@code <td>}.
 *
 * @since 7.19.0
 */
public final class HorizontalTable {
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
   * @return header cells of all rows
   * @since 7.19.0
   */
  public ElementsCollection headers() {
    return root.$$x(".//tr/th");
  }

  /**
   * @param header exact (trimmed) header text
   * @return the value cell of the row with the given header
   * @since 7.19.0
   */
  public SelenideElement value(String header) {
    WebElementCondition hasHeader = match("header = \"" + header + "\"",
      tr -> tr.findElements(xpath("./th")).stream().anyMatch(th -> th.getText().trim().equals(header)));
    return root.$$x(".//tr").findBy(hasHeader).$x("./td");
  }
}
