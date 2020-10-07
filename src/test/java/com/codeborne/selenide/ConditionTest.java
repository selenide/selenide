package com.codeborne.selenide;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.and;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.be;
import static com.codeborne.selenide.Condition.checked;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.cssValue;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactTextCaseSensitive;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.have;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.id;
import static com.codeborne.selenide.Condition.matchesText;
import static com.codeborne.selenide.Condition.name;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.textCaseSensitive;
import static com.codeborne.selenide.Condition.type;
import static com.codeborne.selenide.Condition.visible;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ConditionTest {
  private final WebDriver webDriver = mock(WebDriver.class);
  private final SelenideProxyServer proxy = mock(SelenideProxyServer.class);
  private final SelenideConfig config = new SelenideConfig();
  private final Driver driver = new DriverStub(config, new Browser("opera", false), webDriver, proxy);

  @Test
  void displaysHumanReadableName() {
    assertThat(visible).hasToString("visible");
    assertThat(hidden).hasToString("hidden");
    assertThat(attribute("lastName", "Malkovich")).hasToString("attribute lastName=\"Malkovich\"");
  }

  @Test
  void textConditionChecksForSubstring() {
    assertThat(text("John Malkovich The First").apply(driver, elementWithText("John Malkovich The First")))
      .isTrue();
    assertThat(text("John Malkovich First").apply(driver, elementWithText("John Malkovich The First")))
      .isFalse();
    assertThat(text("john bon jovi").apply(driver, elementWithText("John Malkovich The First")))
      .isFalse();
  }

  private WebElement elementWithText(String text) {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn(text);
    return element;
  }

  @Test
  void textConditionIsCaseInsensitive() {
    WebElement element = elementWithText("John Malkovich The First");
    assertThat(text("john malkovich").apply(driver, element)).isTrue();
  }

  @Test
  void textConditionIgnoresWhitespaces() {
    assertThat(text("john the malkovich").apply(driver, elementWithText("John  the\n Malkovich")))
      .isTrue();
    assertThat(text("This is nonbreakable space").apply(driver, elementWithText("This is nonbreakable\u00a0space")))
      .isTrue();
  }

  @Test
  void testTextCaseSensitive() {
    WebElement element = elementWithText("John Malkovich The First");
    assertThat(textCaseSensitive("john malkovich").apply(driver, element)).isFalse();
    assertThat(textCaseSensitive("John Malkovich").apply(driver, element)).isTrue();
  }

  @Test
  void textCaseSensitiveIgnoresWhitespaces() {
    WebElement element = elementWithText("John Malkovich\t The   \n First");
    assertThat(textCaseSensitive("john malkovich").apply(driver, element)).isFalse();
    assertThat(textCaseSensitive("John        Malkovich The   ").apply(driver, element)).isTrue();
  }

  @Test
  void textCaseSensitiveToString() {
    assertThat(textCaseSensitive("John Malcovich")).hasToString("textCaseSensitive 'John Malcovich'");
  }

  @Test
  void exactTextIsCaseInsensitive() {
    WebElement element = elementWithText("John Malkovich");
    assertThat(exactText("john malkovich").apply(driver, element)).isTrue();
    assertThat(exactText("john").apply(driver, element)).isFalse();
  }

  @Test
  void exactTextToString() {
    assertThat(exactText("John Malcovich")).hasToString("exact text 'John Malcovich'");
  }

  @Test
  void testExactTextCaseSensitive() {
    WebElement element = elementWithText("John Malkovich");
    assertThat(exactTextCaseSensitive("john malkovich").apply(driver, element)).isFalse();
    assertThat(exactTextCaseSensitive("John Malkovich").apply(driver, element)).isTrue();
    assertThat(exactTextCaseSensitive("John").apply(driver, element)).isFalse();
  }

  @Test
  void exactTextCaseSensitiveToString() {
    assertThat(exactTextCaseSensitive("John Malcovich"))
      .hasToString("exact text case sensitive 'John Malcovich'");
  }

  @Test
  void value() {
    WebElement element = elementWithAttribute("value", "John Malkovich");
    assertThat(Condition.value("Peter").apply(driver, element)).isFalse();
    assertThat(Condition.value("john").apply(driver, element)).isTrue();
    assertThat(Condition.value("john malkovich").apply(driver, element)).isTrue();
    assertThat(Condition.value("John").apply(driver, element)).isTrue();
    assertThat(Condition.value("John Malkovich").apply(driver, element)).isTrue();
    assertThat(Condition.value("malko").apply(driver, element)).isTrue();
  }

  private WebElement elementWithAttribute(String name, String value) {
    WebElement element = mock(WebElement.class);
    when(element.getAttribute(name)).thenReturn(value);
    return element;
  }

  @Test
  void valueToString() {
    assertThat(Condition.value("John Malkovich"))
      .hasToString("value 'John Malkovich'");
  }

  @Test
  void elementIsVisible() {
    assertThat(visible.apply(driver, elementWithVisibility(true))).isTrue();
    assertThat(visible.apply(driver, elementWithVisibility(false))).isFalse();
  }

  private WebElement elementWithVisibility(boolean isVisible) {
    WebElement element = mock(WebElement.class);
    when(element.isDisplayed()).thenReturn(isVisible);
    return element;
  }

  @Test
  void elementExists() {
    assertThat(exist.apply(driver, elementWithVisibility(true))).isTrue();
    assertThat(exist.apply(driver, elementWithVisibility(false))).isTrue();
  }

  @Test
  void elementExists_returnsFalse_ifItThrowsException() {
    WebElement element = mock(WebElement.class);
    when(element.isDisplayed()).thenThrow(new StaleElementReferenceException("ups"));
    assertThat(exist.apply(driver, element)).isFalse();
  }

  @Test
  void elementIsHidden() {
    assertThat(hidden.apply(driver, elementWithVisibility(false))).isTrue();
    assertThat(hidden.apply(driver, elementWithVisibility(true))).isFalse();
  }

  @Test
  void elementIsHiddenWithStaleElementException() {
    WebElement element = mock(WebElement.class);
    doThrow(new StaleElementReferenceException("Oooops")).when(element).isDisplayed();
    assertThat(hidden.apply(driver, element)).isTrue();
  }

  @Test
  void elementHasAttribute() {
    assertThat(attribute("name").apply(driver, elementWithAttribute("name", "selenide"))).isTrue();
    assertThat(attribute("name").apply(driver, elementWithAttribute("name", ""))).isTrue();
    assertThat(attribute("name").apply(driver, elementWithAttribute("id", "id3"))).isFalse();
  }

  @Test
  void elementHasAttributeWithGivenValue() {
    assertThat(attribute("name", "selenide").apply(driver, elementWithAttribute("name", "selenide"))).isTrue();
    assertThat(attribute("name", "selenide").apply(driver, elementWithAttribute("name", "selenide is great"))).isFalse();
    assertThat(attribute("name", "selenide").apply(driver, elementWithAttribute("id", "id2"))).isFalse();
  }

  @Test
  void elementHasAttributeMatching() {
    assertThat(attributeMatching("name", "selenide").apply(driver, elementWithAttribute("name", "selenide"))).isTrue();
    assertThat(attributeMatching("name", "selenide.*").apply(driver, elementWithAttribute("name", "selenide is great"))).isTrue();
    assertThat(attributeMatching("name", "selenide.*").apply(driver, elementWithAttribute("id", "selenide"))).isFalse();
    assertThat(attributeMatching("name", "value.*").apply(driver, elementWithAttribute("name", "another value"))).isFalse();
  }

  @Test
  void elementHasValue() {
    assertThat(Condition.value("selenide").apply(driver, elementWithAttribute("value", "selenide"))).isTrue();
    assertThat(Condition.value("selenide").apply(driver, elementWithAttribute("value", "selenide is great"))).isTrue();
    assertThat(Condition.value("selenide").apply(driver, elementWithAttribute("value", "is great"))).isFalse();
  }

  @Test
  void elementHasName() {
    assertThat(name("selenide").apply(driver, elementWithAttribute("name", "selenide"))).isTrue();
    assertThat(name("selenide").apply(driver, elementWithAttribute("name", "selenide is great"))).isFalse();
  }

  @Test
  void elementHasType() {
    assertThat(type("selenide").apply(driver, elementWithAttribute("type", "selenide"))).isTrue();
    assertThat(type("selenide").apply(driver, elementWithAttribute("type", "selenide is great"))).isFalse();
  }

  @Test
  void elementHasId() {
    assertThat(id("selenide").apply(driver, elementWithAttribute("id", "selenide"))).isTrue();
    assertThat(id("selenide").apply(driver, elementWithAttribute("id", "selenide is great"))).isFalse();
  }

  @Test
  void elementMatchesText() {
    assertThat(matchesText("selenide").apply(driver, elementWithText("selenidehello"))).isTrue();
    assertThat(matchesText("selenide").apply(driver, elementWithText("  this is  selenide  the great "))).isTrue();
    assertThat(matchesText("selenide\\s+hello\\s*").apply(driver, elementWithText("selenide    hello"))).isTrue();
    assertThat(matchesText("selenide").apply(driver, elementWithText("selenite"))).isFalse();
  }

  @Test
  void elementMatchTextToString() {
    assertThat(matchesText("John Malcovich")).hasToString("match text 'John Malcovich'");
  }

  @Test
  void elementHasClass() {
    assertThat(cssClass("btn").apply(driver, elementWithAttribute("class", "btn btn-warning"))).isTrue();
    assertThat(cssClass("btn-warning").apply(driver, elementWithAttribute("class", "btn btn-warning"))).isTrue();
    assertThat(cssClass("active").apply(driver, elementWithAttribute("class", "btn btn-warning"))).isFalse();
    assertThat(cssClass("").apply(driver, elementWithAttribute("class", "btn btn-warning active"))).isFalse();
    assertThat(cssClass("active").apply(driver, elementWithAttribute("href", "no-class"))).isFalse();
  }

  @Test
  void elementHasCssValue() {
    assertThat(cssValue("display", "none").apply(driver, elementWithCssStyle("display", "none"))).isTrue();
    assertThat(cssValue("font-size", "24").apply(driver, elementWithCssStyle("font-size", "20"))).isFalse();
  }

  private WebElement elementWithCssStyle(String propertyName, String value) {
    WebElement element = mock(WebElement.class);
    when(element.getCssValue(propertyName)).thenReturn(value);
    return element;
  }

  @Test
  void elementHasClassToString() {
    assertThat(cssClass("Foo")).hasToString("css class 'Foo'");
  }

  @Test
  void elementEnabled() {
    assertThat(enabled.apply(driver, elementWithEnabled(true))).isTrue();
    assertThat(enabled.apply(driver, elementWithEnabled(false))).isFalse();
  }

  private WebElement elementWithEnabled(boolean isEnabled) {
    WebElement element = mock(WebElement.class);
    when(element.isEnabled()).thenReturn(isEnabled);
    return element;
  }

  @Test
  void elementEnabledActualValue() {
    assertThat(enabled.actualValue(driver, elementWithEnabled(true))).isEqualTo("enabled");
    assertThat(enabled.actualValue(driver, elementWithEnabled(false))).isEqualTo("disabled");
  }

  @Test
  void elementDisabled() {
    assertThat(disabled.apply(driver, elementWithEnabled(false))).isTrue();
    assertThat(disabled.apply(driver, elementWithEnabled(true))).isFalse();
  }

  @Test
  void elementDisabledActualValue() {
    assertThat(disabled.actualValue(driver, elementWithEnabled(true))).isEqualTo("enabled");
    assertThat(disabled.actualValue(driver, elementWithEnabled(false))).isEqualTo("disabled");
  }

  @Test
  void elementSelected() {
    assertThat(selected.apply(driver, elementWithSelected(true))).isTrue();
    assertThat(selected.apply(driver, elementWithSelected(false))).isFalse();
  }

  private WebElement elementWithSelected(boolean isSelected) {
    WebElement element = mock(WebElement.class);
    when(element.isSelected()).thenReturn(isSelected);
    return element;
  }

  @Test
  void elementSelectedActualValue() {
    assertThat(selected.actualValue(driver, elementWithSelected(true))).isEqualTo("true");
    assertThat(selected.actualValue(driver, elementWithSelected(false))).isEqualTo("false");
  }

  @Test
  void elementChecked() {
    assertThat(checked.apply(driver, elementWithSelected(true))).isTrue();
    assertThat(checked.apply(driver, elementWithSelected(false))).isFalse();
  }

  @Test
  void elementCheckedActualValue() {
    assertThat(checked.actualValue(driver, elementWithSelected(true))).isEqualTo("true");
    assertThat(checked.actualValue(driver, elementWithSelected(false))).isEqualTo("false");
  }

  @Test
  void elementNotCondition() {
    assertThat(not(checked).apply(driver, elementWithSelected(false))).isTrue();
    assertThat(not(checked).apply(driver, elementWithSelected(true))).isFalse();
  }

  @Test
  void elementNotConditionActualValue() {
    assertThat(not(checked).actualValue(driver, elementWithSelected(false))).isEqualTo("false");
    assertThat(not(checked).actualValue(driver, elementWithSelected(true))).isEqualTo("true");
  }

  @Test
  void elementAndCondition() {
    WebElement element = mockElement(true, "text");
    assertThat(and("selected with text", be(selected), have(text("text"))).apply(driver, element))
      .isTrue();
    assertThat(and("selected with text", not(be(selected)), have(text("text")))
      .apply(driver, element))
      .isFalse();
    assertThat(and("selected with text", be(selected), have(text("incorrect"))).apply(driver, element))
      .isFalse();
  }

  @Test
  void elementAndConditionActualValue() {
    WebElement element = mockElement(false, "text");
    Condition condition = and("selected with text", be(selected), have(text("text")));
    assertThat(condition.actualValue(driver, element)).isNullOrEmpty();
    assertThat(condition.apply(driver, element)).isFalse();
    assertThat(condition.actualValue(driver, element)).isEqualTo("false");
  }

  @Test
  void elementAndConditionToString() {
    WebElement element = mockElement(false, "text");
    Condition condition = and("selected with text", be(selected), have(text("text")));
    assertThat(condition).hasToString("selected with text");
    assertThat(condition.apply(driver, element)).isFalse();
    assertThat(condition).hasToString("be selected");
  }

  @Test
  void elementOrCondition() {
    WebElement element = mockElement(false, "text");
    when(element.isDisplayed()).thenReturn(true);
    assertThat(or("Visible, not Selected", visible, checked).apply(driver, element)).isTrue();
    assertThat(or("Selected with text", checked, text("incorrect")).apply(driver, element)).isFalse();
  }

  @Test
  void elementOrConditionActualValue() {
    WebElement element = mockElement(false, "text");
    Condition condition = or("selected with text", be(selected), have(text("text")));
    assertThat(condition.actualValue(driver, element)).isEqualTo("false, null");
    assertThat(condition.apply(driver, element)).isTrue();
  }

  @Test
  void elementOrConditionToString() {
    WebElement element = mockElement(false, "text");
    Condition condition = or("selected with text", be(selected), have(text("text")));
    assertThat(condition).hasToString("selected with text: be selected or have text 'text'");
    assertThat(condition.apply(driver, element)).isTrue();
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
  void conditionApplyNull() {
    Condition condition = attribute("name");
    assertThat(condition.applyNull()).isFalse();
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
