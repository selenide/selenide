package integration;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byRole;
import static com.codeborne.selenide.TextMatchOptions.partialText;
import static org.assertj.core.api.Assertions.assertThat;

final class TableRecipesTest extends ITest {
  private static final Duration TIMEOUT = Duration.ofSeconds(5);

  @BeforeEach
  void openPage() {
    openFile("tables.html");
  }

  @Test
  void cellByRowTextAndIndex() {
    $$("#customers tbody tr").findBy(text("Ernst Handel")).$("td", 2)
      .shouldHave(exactText("Austria"), TIMEOUT);
  }

  @Test
  void cellByRowAccessibleNameAndRole() {
    $("#customers").find(byRole("row", "Ernst Handel", partialText())).find(byRole("cell"), 2)
      .shouldHave(exactText("Austria"), TIMEOUT);
  }

  @Test
  void headerTextsByRole() {
    $("#customers").$$(byRole("columnheader")).shouldHave(exactTexts("Company", "Contact", "Country"));
  }

  @Test
  void cellByColumnHeaderIndex() {
    SelenideElement table = $("#customers");
    int country = table.$$("thead th").shouldHave(itemWithText("Country")).texts().indexOf("Country");

    assertThat(country).isEqualTo(2);
    table.$$("tbody tr").findBy(text("Ernst Handel")).$("td", country).shouldHave(exactText("Austria"), TIMEOUT);
  }

  @Test
  void rowByValueInSpecificColumnWithXpath() {
    SelenideElement table = $("#offices");
    String country = "./tbody/tr[td[count(../../../thead/tr/th[normalize-space()='Country']/preceding-sibling::th) + 1]"
                     + "[normalize-space()='Austria']]";
    String company = "./tbody/tr[td[count(../../../thead/tr/th[normalize-space()='Company']/preceding-sibling::th) + 1]"
                     + "[normalize-space()='Austria']]";

    table.$$x(country).shouldHave(size(2));
    table.$$x(country).first().shouldHave(text("Alpine"));
    table.$$x(company).shouldHave(size(0));
    table.$$("tbody tr").filterBy(text("Austria")).shouldHave(size(3));
  }

  @Test
  void columnValuesByHeader() {
    ElementsCollection headers = $("#offices").$$("thead th");
    int company = headers.shouldHave(itemWithText("Company")).texts().indexOf("Company");

    $("#offices").$$("tbody tr > td:nth-child(" + (company + 1) + ")")
      .shouldHave(exactTexts("Alpine", "Austria Tabak", "Edelweiss"));
    $("#offices").$$x("./tbody/tr/td[count(../../../thead/tr/th[normalize-space()='Country']/preceding-sibling::th) + 1]")
      .shouldHave(exactTexts("Austria", "Germany", "Austria"));
  }

  @Test
  void nestedTablesAreExcludedWithDirectChildSelectors() {
    SelenideElement table = $("#nested");

    table.$$(":scope > tbody > tr").shouldHave(size(1));
    table.$$x("./tbody/tr").shouldHave(size(1));
    table.$$x("./thead/tr/th").shouldHave(exactTexts("Country", "Company"));
    table.$$(byRole("row")).shouldHave(size(4));
    table.$$(byRole("columnheader")).shouldHave(size(4));
  }

  @Test
  void ariaGridWithExplicitRoles() {
    SelenideElement grid = $("#aria-grid");

    grid.$$(byRole("columnheader")).shouldHave(exactTexts("Country", "Company"));
    grid.$$(byRole("row")).shouldHave(size(3));
    grid.find(byRole("row", "Germany", partialText())).find(byRole("gridcell"), 1).shouldHave(exactText("Berglunds"));
    grid.$$(byRole("gridcell")).shouldHave(exactTexts("Austria", "Alpine", "Germany", "Berglunds"));
  }

  @Test
  void rowHeaderRoleWithSiblingCell() {
    SelenideElement specs = $("#specs");

    specs.$$(byRole("rowheader")).shouldHave(exactTexts("Name", "Telephone"));
    specs.find(byRole("rowheader", "Telephone")).sibling(0).shouldHave(exactText("555 77 854"));
  }

  @Test
  void capturedCellSurvivesTableRemount() {
    SelenideElement cell = $("#customers").find(byRole("row", "Ernst Handel", partialText())).find(byRole("cell"), 2);
    cell.shouldHave(exactText("Austria"), TIMEOUT);

    driver().executeJavaScript("window.remountCustomers()");

    cell.shouldHave(exactText("Austria reloaded"), TIMEOUT);
  }
}
