package com.codeborne.selenide.table;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.CheckResult.rejected;

final class ColumnValueCondition extends WebElementCondition {
  private final Table table;
  private final String column;
  private final String value;

  ColumnValueCondition(Table table, String column, String value) {
    super(column + " = \"" + value + "\"");
    this.table = table;
    this.column = column;
    this.value = value;
  }

  @Override
  public CheckResult check(Driver driver, WebElement row) {
    List<String> headers = table.headers().texts();
    if (headers.isEmpty()) {
      return rejected("no headers displayed yet", headers);
    }
    int index = ColumnResolver.indexOf(headers, column, table.root().toString());
    List<WebElement> cells = row.findElements(table.layout().cells());
    if (index >= cells.size()) {
      return rejected("row has " + cells.size() + " cells", cells.size());
    }
    String actual = ColumnResolver.normalize(cells.get(index).getText());
    return new CheckResult(actual.equals(ColumnResolver.normalize(value)), actual);
  }
}
