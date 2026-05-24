package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byRole;

final class ByRoleTest extends ITest {
  @BeforeEach
  void openPage() {
    openFile("page_with_aria_roles.html");
  }

  @Test
  void findsElementByImplicitButtonRole() {
    $(byRole("button")).shouldHave(attribute("id", "implicit-button"));
  }

  @Test
  void findsElementByImplicitLinkRole() {
    $(byRole("link")).shouldHave(attribute("id", "implicit-link"));
  }

  @Test
  void findsElementByImplicitHeadingRole() {
    $$(byRole("heading")).shouldHave(size(3));
  }

  @Test
  void findsElementByImplicitCheckboxRole() {
    $(byRole("checkbox")).shouldHave(attribute("id", "implicit-checkbox"));
  }

  @Test
  void findsElementByImplicitRadioRole() {
    $(byRole("radio")).shouldHave(attribute("id", "implicit-radio"));
  }

  @Test
  void findsElementByImplicitTextboxRole() {
    $(byRole("textbox")).shouldHave(attribute("id", "implicit-textbox"));
  }

  @Test
  void findsElementByImplicitSearchboxRole() {
    $(byRole("searchbox")).shouldHave(attribute("id", "implicit-searchbox"));
  }

  @Test
  void findsElementByImplicitComboboxRole() {
    $(byRole("combobox")).shouldHave(attribute("id", "implicit-combobox"));
  }

  @Test
  void findsElementByImplicitListboxRole() {
    $(byRole("listbox")).shouldHave(attribute("id", "implicit-listbox"));
  }

  @Test
  void findsElementByImplicitImgRole() {
    $(byRole("img")).shouldHave(attribute("id", "implicit-img"));
  }

  @Test
  void findsElementByImplicitListRole() {
    $(byRole("list")).shouldHave(attribute("id", "implicit-list"));
  }

  @Test
  void findsElementByImplicitListitemRole() {
    $$(byRole("listitem")).shouldHave(size(2));
  }

  @Test
  void findsElementByImplicitNavigationRole() {
    $(byRole("navigation")).shouldHave(attribute("id", "implicit-nav"));
  }

  @Test
  void findsElementByImplicitMainRole() {
    $(byRole("main")).shouldHave(attribute("id", "implicit-main"));
  }

  @Test
  void findsElementByImplicitTableRole() {
    $(byRole("table")).shouldHave(attribute("id", "implicit-table"));
  }

  @Test
  void findsElementByImplicitColumnheaderRole() {
    $(byRole("columnheader")).shouldHave(attribute("id", "implicit-columnheader"));
  }

  @Test
  void findsElementByImplicitRowheaderRole() {
    $(byRole("rowheader")).shouldHave(attribute("id", "implicit-rowheader"));
  }

  @Test
  void findsElementByImplicitCellRole() {
    $(byRole("cell")).shouldHave(text("Alice"));
  }

  @Test
  void findsElementByExplicitRole() {
    $(byRole("alert")).shouldHave(attribute("id", "explicit-alert"));
  }

  @Test
  void explicitRoleOverridesImplicit() {
    $$(byRole("link")).shouldHave(size(2));
    $(byRole("link")).shouldHave(attribute("id", "implicit-link"));
  }
}
