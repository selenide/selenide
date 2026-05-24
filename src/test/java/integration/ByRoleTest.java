package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byRole;
import static com.codeborne.selenide.TextMatchOptions.fullText;
import static com.codeborne.selenide.TextMatchOptions.partialText;

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
  void findsElementByImplicitRowRole() {
    $(byRole("row")).shouldHave(attribute("id", "implicit-row"));
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

  @Test
  void matchesAccessibleNameFromTextContent() {
    $(byRole("button", "Save")).shouldHave(attribute("id", "name-text"));
  }

  @Test
  void matchesAccessibleNameFromAriaLabel() {
    $(byRole("button", "Close dialog")).shouldHave(attribute("id", "name-aria-label"));
  }

  @Test
  void matchesAccessibleNameFromAriaLabelledby() {
    $(byRole("button", "Profile")).shouldHave(attribute("id", "name-aria-labelledby"));
  }

  @Test
  void matchesAccessibleNameFromWrappingLabel() {
    $(byRole("textbox", "Username")).shouldHave(attribute("id", "name-wrapping-label"));
  }

  @Test
  void matchesAccessibleNameFromLabelFor() {
    $(byRole("textbox", "Email address")).shouldHave(attribute("id", "email-field"));
  }

  @Test
  void matchesAccessibleNameFromImgAlt() {
    $(byRole("img", "A friendly cat")).shouldHave(attribute("id", "implicit-img"));
  }

  @Test
  void matchesAccessibleNameFromTitleFallback() {
    $(byRole("button", "Help")).shouldHave(attribute("id", "name-title-fallback"));
  }

  @Test
  void noNameMatch_doesNotFindElement() {
    $(byRole("button", "Nonexistent")).shouldNot(exist);
  }

  @Test
  void matchesAccessibleNameWithPartialOption() {
    $(byRole("button", "ave", partialText())).shouldHave(attribute("id", "name-text"));
  }

  @Test
  void matchesAccessibleNameCaseInsensitively() {
    $(byRole("button", "SUBMIT", fullText().caseInsensitive()))
      .shouldHave(attribute("id", "implicit-button"));
  }

  @Test
  void matchesAccessibleNamePartialAndCaseInsensitively() {
    // Only "Confirm" contains "con" (case-insensitive) among button accessible names in the fixture.
    $$(byRole("button", "con", partialText().caseInsensitive()))
      .shouldHave(size(1));
  }

  @Test
  void multipleButtonsByRoleOnly() {
    // implicit-button, name-text, name-aria-label, name-aria-labelledby, name-title-fallback,
    // cancel-btn, confirm-btn (all <button>), plus div role="button" id="explicit-button".
    // The <button role="link"> is excluded (its effective role is "link").
    $$(byRole("button")).shouldHave(size(8));
  }
}
