package com.codeborne.selenide;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static com.codeborne.selenide.Condition.and;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.be;
import static com.codeborne.selenide.Condition.checked;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.cssValue;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.have;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.id;
import static com.codeborne.selenide.Condition.name;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.type;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Mocks.elementWithAttribute;
import static com.codeborne.selenide.TextCheck.PARTIAL_TEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ConditionTest {
  private final WebDriver webDriver = new DummyWebDriver();
  private final SelenideProxyServer proxy = mock(SelenideProxyServer.class);
  private final SelenideConfig config = new SelenideConfig().textCheck(PARTIAL_TEXT);
  private final Driver driver = new DriverStub(config, new Browser("opera", false), webDriver, proxy);

  @Test
  void displaysHumanReadableName() {
    assertThat(visible).hasToString("visible");
    assertThat(hidden).hasToString("hidden");
    assertThat(attribute("lastName", "Malkovich")).hasToString("attribute lastName=\"Malkovich\"");
  }

  @Test
  void valueAttribute() {
    WebElement element = elementWithAttribute("value", "John Malkovich");
    assertThat(value("Peter").check(driver, element).verdict()).isEqualTo(REJECT);
    assertThat(value("john").check(driver, element).verdict()).isEqualTo(ACCEPT);
    assertThat(value("john malkovich").check(driver, element).verdict()).isEqualTo(ACCEPT);
    assertThat(value("John").check(driver, element).verdict()).isEqualTo(ACCEPT);
    assertThat(value("John Malkovich").check(driver, element).verdict()).isEqualTo(ACCEPT);
    assertThat(value("malko").check(driver, element).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void valueToString() {
    assertThat(value("John Malkovich"))
      .hasToString("value=\"John Malkovich\"");
  }

  @Test
  void elementIsVisible() {
    assertThat(visible.check(driver, elementWithVisibility(true)).verdict()).isEqualTo(ACCEPT);
    assertThat(visible.check(driver, elementWithVisibility(false)).verdict()).isEqualTo(REJECT);
  }

  private WebElement elementWithVisibility(boolean isVisible) {
    WebElement element = mock(WebElement.class);
    when(element.isDisplayed()).thenReturn(isVisible);
    return element;
  }

  @Test
  void elementExists() {
    assertThat(exist.check(driver, elementWithVisibility(true)).verdict()).isEqualTo(ACCEPT);
    assertThat(exist.check(driver, elementWithVisibility(false)).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void elementExists_returnsFalse_ifItThrowsException() {
    WebElement element = mock(WebElement.class);
    when(element.isDisplayed()).thenThrow(new StaleElementReferenceException("ups"));
    assertThat(exist.check(driver, element).verdict()).isEqualTo(REJECT);
  }

  @Test
  void elementIsHidden() {
    assertThat(hidden.check(driver, elementWithVisibility(false)).verdict()).isEqualTo(ACCEPT);
    assertThat(hidden.check(driver, elementWithVisibility(true)).verdict()).isEqualTo(REJECT);
  }

  @Test
  void elementIsHiddenWithStaleElementException() {
    WebElement element = mock(WebElement.class);
    doThrow(new StaleElementReferenceException("Oooops")).when(element).isDisplayed();
    assertThat(hidden.check(driver, element).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void elementHasAttribute() {
    assertThat(attribute("name").check(driver, elementWithAttribute("name", "selenide")).verdict()).isEqualTo(ACCEPT);
    assertThat(attribute("name").check(driver, elementWithAttribute("name", "")).verdict()).isEqualTo(ACCEPT);
    assertThat(attribute("name").check(driver, elementWithAttribute("id", "id3")).verdict()).isEqualTo(REJECT);
  }

  @Test
  void elementHasAttributeWithGivenValue() {
    Condition condition = attribute("name", "selenide");
    assertThat(condition.check(driver, elementWithAttribute("name", "selenide")).verdict()).isEqualTo(ACCEPT);
    assertThat(condition.check(driver, elementWithAttribute("name", "selenide is great")).verdict()).isEqualTo(REJECT);
    assertThat(condition.check(driver, elementWithAttribute("id", "id2")).verdict()).isEqualTo(REJECT);
  }

  @Test
  void matchingAttributeWithRegex() {
    Condition condition = attributeMatching("name", "selenide.*");
    assertThat(condition.check(driver, elementWithAttribute("name", "selenide")).verdict()).isEqualTo(ACCEPT);
    assertThat(condition.check(driver, elementWithAttribute("name", "selenide is great")).verdict()).isEqualTo(ACCEPT);
    assertThat(condition.check(driver, elementWithAttribute("id", "selenide")).verdict()).isEqualTo(REJECT);
    assertThat(condition.check(driver, elementWithAttribute("name", "another selenide")).verdict()).isEqualTo(REJECT);
  }

  @Test
  void elementHasValue() {
    assertThat(value("selenide").check(driver, elementWithAttribute("value", "selenide")).verdict()).isEqualTo(ACCEPT);
    assertThat(value("selenide").check(driver, elementWithAttribute("value", "selenide is great")).verdict()).isEqualTo(ACCEPT);
    assertThat(value("selenide").check(driver, elementWithAttribute("value", "is great")).verdict()).isEqualTo(REJECT);
  }

  @Test
  void elementHasName() {
    assertThat(name("selenide").check(driver, elementWithAttribute("name", "selenide")).verdict()).isEqualTo(ACCEPT);
    assertThat(name("selenide").check(driver, elementWithAttribute("name", "selenide is great")).verdict()).isEqualTo(REJECT);
  }

  @Test
  void checksValueOfTypeAttribute() {
    assertThat(type("radio").check(driver, elementWithAttribute("type", "radio")).verdict()).isEqualTo(ACCEPT);
    assertThat(type("radio").check(driver, elementWithAttribute("type", "radio-button")).verdict()).isEqualTo(REJECT);
  }

  @Test
  void checksValueOfIdAttribute() {
    assertThat(id("selenide").check(driver, elementWithAttribute("id", "selenide")).verdict()).isEqualTo(ACCEPT);
    assertThat(id("selenide").check(driver, elementWithAttribute("id", "selenide is great")).verdict()).isEqualTo(REJECT);
  }

  @Test
  void checksValueOfClassAttribute() {
    assertThat(cssClass("btn").check(driver, elementWithAttribute("class", "btn btn-warning")).verdict()).isEqualTo(ACCEPT);
    assertThat(cssClass("btn-warning").check(driver, elementWithAttribute("class", "btn btn-warning")).verdict()).isEqualTo(ACCEPT);
    assertThat(cssClass("active").check(driver, elementWithAttribute("class", "btn btn-warning")).verdict()).isEqualTo(REJECT);
    assertThat(cssClass("").check(driver, elementWithAttribute("class", "btn btn-warning active")).verdict()).isEqualTo(REJECT);
    assertThat(cssClass("active").check(driver, elementWithAttribute("href", "no-class")).verdict()).isEqualTo(REJECT);
  }

  @Test
  void elementHasCssValue() {
    assertThat(cssValue("display", "none").check(driver, elementWithCssStyle("display", "none")).verdict()).isEqualTo(ACCEPT);
    assertThat(cssValue("font-size", "24").check(driver, elementWithCssStyle("font-size", "20")).verdict()).isEqualTo(REJECT);
  }

  private WebElement elementWithCssStyle(String propertyName, String value) {
    WebElement element = mock(WebElement.class);
    when(element.getCssValue(propertyName)).thenReturn(value);
    return element;
  }

  @Test
  void elementHasClassToString() {
    assertThat(cssClass("Foo")).hasToString("css class \"Foo\"");
  }

  @Test
  void elementEnabled() {
    assertThat(enabled.check(driver, elementWithEnabled(true)).verdict()).isEqualTo(ACCEPT);
    assertThat(enabled.check(driver, elementWithEnabled(false)).verdict()).isEqualTo(REJECT);
  }

  private WebElement elementWithEnabled(boolean isEnabled) {
    WebElement element = mock(WebElement.class);
    when(element.isEnabled()).thenReturn(isEnabled);
    return element;
  }

  @Test
  void elementEnabledActualValue() {
    assertThat(enabled.check(driver, elementWithEnabled(true)).actualValue()).isEqualTo("enabled");
    assertThat(enabled.check(driver, elementWithEnabled(false)).actualValue()).isEqualTo("disabled");
  }

  @Test
  void elementDisabled() {
    assertThat(disabled.check(driver, elementWithEnabled(false)).verdict()).isEqualTo(ACCEPT);
    assertThat(disabled.check(driver, elementWithEnabled(true)).verdict()).isEqualTo(REJECT);
  }

  @Test
  void elementDisabledActualValue() {
    assertThat(disabled.check(driver, elementWithEnabled(true)).actualValue()).isEqualTo("enabled");
    assertThat(disabled.check(driver, elementWithEnabled(false)).actualValue()).isEqualTo("disabled");
  }

  @Test
  void elementSelected() {
    assertThat(selected.check(driver, elementWithSelected(true)).verdict()).isEqualTo(ACCEPT);
    assertThat(selected.check(driver, elementWithSelected(false)).verdict()).isEqualTo(REJECT);
  }

  private WebElement elementWithSelected(boolean isSelected) {
    WebElement element = mock(WebElement.class);
    when(element.isSelected()).thenReturn(isSelected);
    return element;
  }

  @Test
  void elementSelectedActualValue() {
    assertThat(selected.check(driver, elementWithSelected(true)).actualValue()).isEqualTo("selected");
    assertThat(selected.check(driver, elementWithSelected(false)).actualValue()).isEqualTo("not selected");
  }

  @Test
  void elementChecked() {
    assertThat(checked.check(driver, elementWithSelected(true)).verdict()).isEqualTo(ACCEPT);
    assertThat(checked.check(driver, elementWithSelected(false)).verdict()).isEqualTo(REJECT);
  }

  @Test
  void elementCheckedActualValue() {
    assertThat(checked.check(driver, elementWithSelected(true)).actualValue()).isEqualTo("checked");
    assertThat(checked.check(driver, elementWithSelected(false)).actualValue()).isEqualTo("unchecked");
  }

  @Test
  void elementNotCondition() {
    assertThat(not(checked).check(driver, elementWithSelected(false)).verdict()).isEqualTo(ACCEPT);
    assertThat(not(checked).check(driver, elementWithSelected(true)).verdict()).isEqualTo(REJECT);
  }

  @Test
  void elementNotConditionActualValue() {
    assertThat(not(checked).check(driver, elementWithSelected(false)).actualValue()).isEqualTo("unchecked");
    assertThat(not(checked).check(driver, elementWithSelected(true)).actualValue()).isEqualTo("checked");
  }

  @Test
  void elementAndCondition() {
    WebElement element = mockElement(true, "text");
    assertThat(and("selected with text", be(selected), have(text("text"))).check(driver, element).verdict()).isEqualTo(ACCEPT);
    assertThat(and("selected with text", not(be(selected)), have(text("text"))).check(driver, element).verdict()).isEqualTo(REJECT);
    assertThat(and("selected with text", be(selected), have(text("incorrect"))).check(driver, element).verdict()).isEqualTo(REJECT);
  }

  @Test
  void elementAndConditionActualValue() {
    WebElement element = mockElement(false, "text");
    Condition condition = and("selected with text", be(selected), have(text("text")));
    assertThat(condition.check(driver, element).actualValue()).isEqualTo("not selected");
    assertThat(condition.check(driver, element).verdict()).isEqualTo(REJECT);
  }

  @Test
  void elementAndConditionToString() {
    WebElement element = mockElement(false, "text");
    Condition condition = and("selected with text", be(selected), have(text("text")));
    assertThat(condition).hasToString("selected with text: be selected and have text \"text\"");
    assertThat(condition.check(driver, element).verdict()).isEqualTo(REJECT);
    assertThat(condition).hasToString("selected with text: be selected and have text \"text\"");
  }

  @Test
  void elementOrCondition() {
    WebElement element = mockElement(false, "text");
    when(element.isDisplayed()).thenReturn(true);
    assertThat(or("Visible, not Selected", visible, checked).check(driver, element).verdict()).isEqualTo(ACCEPT);
    assertThat(or("Selected with text", checked, text("incorrect")).check(driver, element).verdict()).isEqualTo(REJECT);
  }

  @Test
  void elementOrConditionActualValue() {
    WebElement element = mockElement(false, "some text");
    Condition condition = or("selected with text", be(selected), have(text("some text")));
    assertThat(condition.check(driver, element).actualValue()).isEqualTo("text=\"some text\"");
    assertThat(condition.check(driver, element).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void elementOrConditionToString() {
    WebElement element = mockElement(false, "text");
    Condition condition = or("selected with text", be(selected), have(text("text")));
    assertThat(condition).hasToString("selected with text: be selected or have text \"text\"");
    assertThat(condition.check(driver, element).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void conditionBe() {
    Condition condition = be(visible);
    assertThat(condition).hasToString("be visible");
  }

  @Test
  void conditionHave() {
    Condition condition = have(attribute("name"));
    assertThat(condition).hasToString("have attribute name");
  }

  @Test
  void conditionMissingElementSatisfiesCondition() {
    Condition condition = attribute("name");
    assertThat(condition.missingElementSatisfiesCondition()).isFalse();
  }

  @Test
  void conditionToString() {
    Condition condition = attribute("name").because("it's awesome");
    assertThat(condition).hasToString("attribute name (because it's awesome)");
  }

  @Test
  void shouldHaveText_doesNotAccept_nullParameter() {
    //noinspection ConstantConditions
    assertThatThrownBy(() -> text(null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Argument must not be null or empty string. Use $.shouldBe(empty) or $.shouldHave(exactText(\"\").");
  }

  @Test
  void shouldHaveText_doesNotAccept_emptyString() {
    assertThatThrownBy(() -> text(""))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Argument must not be null or empty string. Use $.shouldBe(empty) or $.shouldHave(exactText(\"\").");
  }

  @Test
  void shouldHaveText_accepts_blankNonEmptyString() {
    text(" ");
    text("  ");
    text("\t");
    text("\n");
  }

  private WebElement mockElement(boolean isSelected, String text) {
    WebElement element = mock(WebElement.class);
    when(element.isSelected()).thenReturn(isSelected);
    when(element.getText()).thenReturn(text);
    return element;
  }
}
