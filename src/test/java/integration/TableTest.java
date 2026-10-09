package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.table.Table;
import com.codeborne.selenide.table.TableColumnException;
import com.codeborne.selenide.table.TableLayout;
import com.codeborne.selenide.table.TableRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class TableTest extends ITest {
  private static final Duration TIMEOUT = Duration.ofSeconds(5);

  @BeforeEach
  void openPage() {
    openFile("table_helper.html");
  }

  @Test
  void findsDelayedRowByColumnValue() {
    customers().row("Company", "Ernst Handel").cell("Country").shouldHave(exactText("Austria"), TIMEOUT);
  }

  @Test
  void matchesValueOnlyInGivenColumn() {
    Table t = queryClassic();

    t.rows("Company", "Austria").shouldHave(size(0));
    t.rows("Country", "Austria").shouldHave(size(2));
  }

  @Test
  void returnsAllMatchingRows() {
    Table t = queryClassic();

    t.rows("Country", "Austria").shouldHave(size(2));
    t.row("Country", "Austria").cell("Company").shouldHave(exactText("Alfreds"));
  }

  @Test
  void readsColumnByHeader() {
    customers().column("Company").shouldHave(exactTexts("Alfreds Futterkiste", "Ernst Handel"), TIMEOUT);
  }

  @Test
  void rowsLookupWaitsWhileHeadersMount() {
    Table t = queryClassic();
    driver().executeJavaScript("const table = document.getElementById('query-classic');"
      + "const head = table.tHead; head.remove();"
      + "setTimeout(() => table.insertBefore(head, table.tBodies[0]), 300);");

    t.rows("Company", "Alfreds").shouldHave(size(1), Duration.ofSeconds(2));
  }

  @Test
  void cellWaitsForLateHeader() {
    setTimeout(TIMEOUT.toMillis());
    Table t = queryClassic();
    TableRow berglunds = t.row("Company", "Berglunds");
    driver().executeJavaScript("const tr = document.querySelector('#query-classic thead tr');"
      + "const th = tr.children[2]; th.remove();"
      + "setTimeout(() => tr.appendChild(th), 500);");

    berglunds.cell("Employees").shouldHave(exactText("20"));
  }

  @Test
  void wrapsRowFoundByCaller() {
    Table t = queryClassic();
    TableRow berglunds = t.row(t.rows().findBy(text("Berglunds")));

    berglunds.cell("Country").shouldHave(exactText("Germany"));
    berglunds.cell("Employees").shouldHave(exactText("20"));
  }

  @Test
  void missingHeaderThrowsTableColumnException() {
    Table t = queryClassic();

    assertThatThrownBy(() -> t.row(0).cell("Region"))
      .isInstanceOf(TableColumnException.class)
      .hasMessageContaining("Region")
      .hasMessageContaining("[Country, Company, Employees]");
  }

  @Test
  void duplicateHeaderInRowLookupThrows() {
    Table t = Table.of($("#repeated-table"), TableLayout.html());

    assertThatThrownBy(() -> t.row("Company", "x").self().should(exist, Duration.ofMillis(500)))
      .isInstanceOf(TableColumnException.class)
      .hasMessageContaining("ambiguous");
  }

  @Test
  void rowLookupWaitsForLateColumn() {
    Table t = queryClassic();

    renameEmployeesHeaderTemporarily();
    t.row("Employees", "20").cell("Company").shouldHave(exactText("Berglunds"), Duration.ofSeconds(2));
    renameEmployeesHeaderTemporarily();
    t.rows("Employees", "20").shouldHave(size(1), Duration.ofSeconds(2));
  }

  @Test
  void missingColumnInRowLookupFailsWithElementNotFound() {
    Table t = queryClassic();

    assertThatThrownBy(() -> t.row("Region", "x").self().should(exist, Duration.ofMillis(500)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Region");
  }

  @Test
  void missingRowFailsWithElementNotFound() {
    assertThatThrownBy(() -> customers().row("Company", "Missing Company").self().should(exist, Duration.ofMillis(600)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Company = \"Missing Company\"");
  }

  @Test
  void capturedRowSurvivesRemount() {
    setTimeout(TIMEOUT.toMillis());
    TableRow row = customers().row("Company", "Ernst Handel");

    row.cell("Country").shouldHave(exactText("Austria"), TIMEOUT);
    driver().executeJavaScript("window.reloadClassicTable()");
    row.cell("Country").shouldHave(exactText("Austria reloaded"), TIMEOUT);
  }

  @Test
  void rowLookupSurvivesHeaderReorder() {
    TableRow row = queryClassic().row("Company", "Berglunds");

    row.self().shouldHave(text("Germany"));
    driver().executeJavaScript("window.remountQueryClassicWithReorderedHeaders()");
    row.cell("Country").shouldHave(exactText("Germany"));
  }

  @Test
  void htmlLayoutIgnoresNestedTableRows() {
    Table.of($("#nested-classic"), TableLayout.html()).rows().shouldHave(size(1));
  }

  @Test
  void waitsForLateMountedRoot() {
    openFile("table_helper_late.html");
    Table late = Table.of($("#late-customers"), TableLayout.of(
      By.xpath("./tbody/tr[td]"), By.xpath("./td"), By.xpath("./tbody/tr[th]/th")));

    late.row("Company", "Ernst Handel").self().shouldBe(visible, TIMEOUT);
  }

  @Test
  void readsAriaGrid() {
    Table t = Table.of($("#aria-grid"), TableLayout.aria());

    t.headers().shouldHave(exactTexts("Country", "Company"));
    t.row("Country", "Austria").cell("Company").shouldHave(exactText("Alfreds"));
  }

  @Test
  void readsCustomDivGridWithHiddenHeaderCell() {
    Table grid = Table.of($("#custom-grid"), TableLayout.of(By.cssSelector(":scope > .data-row"),
      By.cssSelector(":scope > .cell"), By.cssSelector(":scope > .header-row > .cell")));

    grid.rows().shouldHave(size(3));
    grid.row("Country", "Austria").cell("Company").shouldHave(text("Outer"));
    grid.rows("Company", "anything").shouldHave(size(0));
  }

  @Test
  void readsFlexTable() {
    flexCustomers().row("Company", "Ernst Handel").cell("Country").shouldHave(exactText("Austria"), TIMEOUT);
  }

  @Test
  void columnRequiresXpathLayout() {
    Table flex = flexCustomers();

    assertThatThrownBy(() -> flex.column("Company")).isInstanceOf(UnsupportedOperationException.class);
  }

  private void renameEmployeesHeaderTemporarily() {
    driver().executeJavaScript("const th = document.querySelector('#query-classic thead tr').children[2];"
      + "th.textContent = 'Staff';"
      + "setTimeout(() => { th.textContent = 'Employees'; }, 300);");
  }

  private Table customers() {
    return Table.of($("#customers"), TableLayout.html());
  }

  private Table queryClassic() {
    return Table.of($("#query-classic"), TableLayout.html());
  }

  private Table flexCustomers() {
    return Table.of($("#flex-customers"), TableLayout.of(
      By.cssSelector(":scope > .flex-table-row:not(:first-child)"), By.cssSelector(":scope > div"),
      By.cssSelector(":scope > .flex-table-row:first-child > div")));
  }
}
