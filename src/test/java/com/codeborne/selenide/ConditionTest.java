package com.codeborne.selenide;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.be;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConditionTest extends UnitTest {
  @Test
  void displaysHumanReadableName() {
    assertThat(Condition.visible)
      .hasToString("visible");
    assertThat(Condition.hidden)
      .hasToString("hidden");
    assertThat(Condition.hasAttribute("lastName", "Malkovich"))
      .hasToString("attribute lastName=Malkovich");
  }

  @Test
  void textConditionChecksForSubstring() {
    assertThat(Condition.text("John Malkovich The First").apply(elementWithText("John Malkovich The First")))
      .isTrue();
    assertThat(Condition.text("John Malkovich First").apply(elementWithText("John Malkovich The First")))
      .isFalse();
    assertThat(Condition.text("john bon jovi").apply(elementWithText("John Malkovich The First")))
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
    assertThat(Condition.text("john malkovich").apply(element))
      .isTrue();
  }

  @Test
  void textConditionIgnoresWhitespaces() {
    assertThat(Condition.text("john the malkovich").apply(elementWithText("John  the\n Malkovich")))
      .isTrue();
    assertThat(Condition.text("This is nonbreakable space").apply(elementWithText("This is nonbreakable\u00a0space")))
      .isTrue();
  }

  @Test
  void textCaseSensitive() {
    WebElement element = elementWithText("John Malkovich The First");
    assertThat(Condition.textCaseSensitive("john malkovich").apply(element))
      .isFalse();
    assertThat(Condition.textCaseSensitive("John Malkovich").apply(element))
      .isTrue();
  }

  @Test
  void textCaseSensitiveIgnoresWhitespaces() {
    WebElement element = elementWithText("John Malkovich\t The   \n First");
    assertThat(Condition.textCaseSensitive("john malkovich").apply(element))
      .isFalse();
    assertThat(Condition.textCaseSensitive("John        Malkovich The   ").apply(element))
      .isTrue();
  }

  @Test
  void textCaseSencitiveToString() {
    assertThat(Condition.textCaseSensitive("John Malcovich"))
      .hasToString("textCaseSensitive 'John Malcovich'");
  }

  @Test
  void exactTextIsCaseInsensitive() {
    WebElement element = elementWithText("John Malkovich");
    assertThat(Condition.exactText("john malkovich").apply(element))
      .isTrue();
    assertThat(Condition.exactText("john").apply(element))
      .isFalse();
  }

  @Test
  void exactTextToString() {
    assertThat(Condition.exactText("John Malcovich"))
      .hasToString("exact text 'John Malcovich'");
  }

  @Test
  void exactTextCaseSensitive() {
    WebElement element = elementWithText("John Malkovich");
    assertThat(Condition.exactTextCaseSensitive("john malkovich").apply(element))
      .isFalse();
    assertThat(Condition.exactTextCaseSensitive("John Malkovich").apply(element))
      .isTrue();
    assertThat(Condition.exactTextCaseSensitive("John").apply(element))
      .isFalse();
  }

  @Test
  void exactTextCaseSensitiveToString() {
    assertThat(Condition.exactTextCaseSensitive("John Malcovich"))
      .hasToString("exact text case sensitive 'John Malcovich'");
  }

  @Test
  void value() {
    WebElement element = elementWithAttribute("value", "John Malkovich");
    assertThat(Condition.value("Peter").apply(element))
      .isFalse();
    assertThat(Condition.value("john").apply(element))
      .isTrue();
    assertThat(Condition.value("john malkovich").apply(element))
      .isTrue();
    assertThat(Condition.value("John").apply(element))
      .isTrue();
    assertThat(Condition.value("John Malkovich").apply(element))
      .isTrue();
    assertThat(Condition.value("malko").apply(element))
      .isTrue();
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
    assertThat(Condition.visible.apply(elementWithVisibility(true)))
      .isTrue();
    assertThat(Condition.visible.apply(elementWithVisibility(false)))
      .isFalse();
  }

  private WebElement elementWithVisibility(boolean isVisible) {
    WebElement element = mock(WebElement.class);
    when(element.isDisplayed()).thenReturn(isVisible);
    return element;
  }

  @Test
  void elementExists() {
    assertThat(Condition.exist.apply(elementWithVisibility(true)))
      .isTrue();
    assertThat(Condition.exist.apply(elementWithVisibility(false)))
      .isTrue();
  }

  @Test
  void elementExists_returnsFalse_ifItThrowsException() {
    WebElement element = mock(WebElement.class);
    when(element.isDisplayed()).thenThrow(new StaleElementReferenceException("ups"));
    assertThat(Condition.exist.apply(element))
      .isFalse();
  }

  @Test
  void elementIsHidden() {
    assertThat(Condition.hidden.apply(elementWithVisibility(false)))
      .isTrue();
    assertThat(Condition.hidden.apply(elementWithVisibility(true)))
      .isFalse();
  }

  @Test
  void elementIsHiddenWithStaleElementException() {
    WebElement element = mock(WebElement.class);
    doThrow(new StaleElementReferenceException("Oooops")).when(element).isDisplayed();
    assertThat(Condition.hidden.apply(element))
      .isTrue();
  }

  @Test
  void elementHasAttribute() {
    assertThat(Condition.attribute("name").apply(elementWithAttribute("name", "selenide")))
      .isTrue();
    assertThat(Condition.attribute("name").apply(elementWithAttribute("name", "")))
      .isTrue();
    assertThat(Condition.attribute("name").apply(elementWithAttribute("id", "id3")))
      .isFalse();
  }

  @Test
  void elementHasAttributeWithGivenValue() {
    assertThat(Condition.attribute("name", "selenide").apply(elementWithAttribute("name", "selenide")))
      .isTrue();
    assertThat(Condition.attribute("name", "selenide").apply(elementWithAttribute("name", "selenide is great")))
      .isFalse();
    assertThat(Condition.attribute("name", "selenide").apply(elementWithAttribute("id", "id2")))
      .isFalse();
  }

  @Test
  void elementHasValue() {
    assertThat(Condition.value("selenide").apply(elementWithAttribute("value", "selenide")))
      .isTrue();
    assertThat(Condition.value("selenide").apply(elementWithAttribute("value", "selenide is great")))
      .isTrue();
    assertThat(Condition.value("selenide").apply(elementWithAttribute("value", "is great")))
      .isFalse();
  }

  @Test
  void elementHasValueMethod() {
    assertThat(Condition.hasValue("selenide").apply(elementWithAttribute("value", "selenide")))
      .isTrue();
    assertThat(Condition.hasValue("selenide").apply(elementWithAttribute("value", "selenide is great")))
      .isTrue();
    assertThat(Condition.hasValue("selenide").apply(elementWithAttribute("value", "is great")))
      .isFalse();
  }

  @Test
  void elementHasName() {
    assertThat(Condition.name("selenide").apply(elementWithAttribute("name", "selenide")))
      .isTrue();
    assertThat(Condition.name("selenide").apply(elementWithAttribute("name", "selenide is great")))
      .isFalse();
  }

  @Test
  void elementHasType() {
    assertThat(Condition.type("selenide").apply(elementWithAttribute("type", "selenide")))
      .isTrue();
    assertThat(Condition.type("selenide").apply(elementWithAttribute("type", "selenide is great")))
      .isFalse();
  }

  @Test
  void elementHasId() {
    assertThat(Condition.id("selenide").apply(elementWithAttribute("id", "selenide")))
      .isTrue();
    assertThat(Condition.id("selenide").apply(elementWithAttribute("id", "selenide is great")))
      .isFalse();
  }

  @Test
  void elementMatchesText() {
    assertThat(Condition.matchesText("selenide").apply(elementWithText("selenidehello")))
      .isTrue();
    assertThat(Condition.matchesText("selenide").apply(elementWithText("  this is  selenide  the great ")))
      .isTrue();
    assertThat(Condition.matchesText("selenide\\s+hello\\s*").apply(elementWithText("selenide    hello")))
      .isTrue();
    assertThat(Condition.matchesText("selenide").apply(elementWithText("selenite")))
      .isFalse();
  }

  @Test
  void elementMatchTextToString() {
    assertThat(Condition.matchesText("John Malcovich"))
      .hasToString("match text 'John Malcovich'");
  }

  @Test
  void elementHasText() {
    assertThat(Condition.hasText("selenide").apply(elementWithText("selenidehello")))
      .isTrue();
    assertThat(Condition.hasText("hello").apply(elementWithText("selenidehello")))
      .isTrue();
    assertThat(Condition.hasText("selenide, hello").apply(elementWithText("selenidehello")))
      .isFalse();
  }

  @Test
  void elementHasClass() {
    assertThat(Condition.hasClass("btn").apply(elementWithAttribute("class", "btn btn-warning")))
      .isTrue();
    assertThat(Condition.hasClass("btn-warning").apply(elementWithAttribute("class", "btn btn-warning")))
      .isTrue();
    assertThat(Condition.hasClass("active").apply(elementWithAttribute("class", "btn btn-warning")))
      .isFalse();
  }

  @Test
  void elementHasCssValue() {
    assertThat(Condition.cssValue("display", "none").apply(elementWithCssStyle("display", "none")))
      .isTrue();
    assertThat(Condition.cssValue("font-size", "24").apply(elementWithCssStyle("font-size", "20")))
      .isFalse();
  }

  private WebElement elementWithCssStyle(String propertyName, String value) {
    WebElement element = mock(WebElement.class);
    when(element.getCssValue(propertyName)).thenReturn(value);
    return element;
  }

  @Test
  void elementHasClassToString() {
    assertThat(Condition.hasClass("Foo"))
      .hasToString("css class 'Foo'");
  }

  @Test
  void elementHasClassForElement() {
    assertThat(Condition.hasClass(elementWithAttribute("class", "btn btn-warning"), "btn"))
      .isTrue();
    assertThat(Condition.hasClass(elementWithAttribute("class", "btn btn-warning"), "btn-warning"))
      .isTrue();
    assertThat(Condition.hasClass(elementWithAttribute("class", "btn btn-warning"), "form-horizontal"))
      .isFalse();
  }

  @Test
  void elementEnabled() {
    assertThat(Condition.enabled.apply(elementWithEnabled(true)))
      .isTrue();
    assertThat(Condition.enabled.apply(elementWithEnabled(false)))
      .isFalse();
  }

  private WebElement elementWithEnabled(boolean isEnabled) {
    WebElement element = mock(WebElement.class);
    when(element.isEnabled()).thenReturn(isEnabled);
    return element;
  }

  @Test
  void elementEnabledActualValue() {
    assertThat(Condition.enabled.actualValue(elementWithEnabled(true)))
      .isEqualTo("enabled");
    assertThat(Condition.enabled.actualValue(elementWithEnabled(false)))
      .isEqualTo("disabled");
  }

  @Test
  void elementDisabled() {
    assertThat(Condition.disabled.apply(elementWithEnabled(false)))
      .isTrue();
    assertThat(Condition.disabled.apply(elementWithEnabled(true)))
      .isFalse();
  }

  @Test
  void elementDisabledActualValue() {
    assertThat(Condition.disabled.actualValue(elementWithEnabled(true)))
      .isEqualTo("enabled");
    assertThat(Condition.disabled.actualValue(elementWithEnabled(false)))
      .isEqualTo("disabled");
  }

  @Test
  void elementSelected() {
    assertThat(Condition.selected.apply(elementWithSelected(true)))
      .isTrue();
    assertThat(Condition.selected.apply(elementWithSelected(false)))
      .isFalse();
  }

  private WebElement elementWithSelected(boolean isSelected) {
    WebElement element = mock(WebElement.class);
    when(element.isSelected()).thenReturn(isSelected);
    return element;
  }

  @Test
  void elementSelectedActualValue() {
    assertThat(Condition.selected.actualValue(elementWithSelected(true)))
      .isEqualTo("true");
    assertThat(Condition.selected.actualValue(elementWithSelected(false)))
      .isEqualTo("false");
  }

  @Test
  void elementChecked() {
    assertThat(Condition.checked.apply(elementWithSelected(true)))
      .isTrue();
    assertThat(Condition.checked.apply(elementWithSelected(false)))
      .isFalse();
  }

  @Test
  void elementCheckedActualValue() {
    assertThat(Condition.checked.actualValue(elementWithSelected(true)))
      .isEqualTo("true");
    assertThat(Condition.checked.actualValue(elementWithSelected(false)))
      .isEqualTo("false");
  }

  @Test
  void elementNotCondition() {
    assertThat(Condition.not(Condition.checked).apply(elementWithSelected(false)))
      .isTrue();
    assertThat(Condition.not(Condition.checked).apply(elementWithSelected(true)))
      .isFalse();
  }

  @Test
  void elementNotCondtionActualValue() {
    assertThat(Condition.not(Condition.checked).actualValue(elementWithSelected(false)))
      .isEqualTo("false");
    assertThat(Condition.not(Condition.checked).actualValue(elementWithSelected(true)))
      .isEqualTo("true");
  }

  @Test
  void elementAndCondition() {
    WebElement element = elementWithSelectedAndText(true, "text");
    assertThat(Condition.and("selected with text", be(Condition.selected), Condition.have(Condition.text("text"))).apply(element))
      .isTrue();
    assertThat(Condition.and("selected with text", Condition.not(be(Condition.selected)), Condition.have(Condition.text("text")))
      .apply(element))
      .isFalse();
    assertThat(Condition.and("selected with text", be(Condition.selected), Condition.have(Condition.text("incorrect"))).apply(element))
      .isFalse();
  }

  private WebElement elementWithSelectedAndText(boolean isSelected, String text) {
    WebElement element = mock(WebElement.class);
    when(element.isSelected()).thenReturn(isSelected);
    when(element.getText()).thenReturn(text);
    return element;
  }

  @Test
  void elementAndConditionActualValue() {
    WebElement element = elementWithSelectedAndText(false, "text");
    Condition condition = Condition.and("selected with text", be(Condition.selected),
      Condition.have(Condition.text("text")));
    assertThat(condition.actualValue(element))
      .isNullOrEmpty();
    assertThat(condition.apply(element))
      .isFalse();
    assertThat(condition.actualValue(element))
      .isEqualTo("false");
  }

  @Test
  void elementAndConditionToString() {
    WebElement element = elementWithSelectedAndText(false, "text");
    Condition condition = Condition.and("selected with text", be(Condition.selected),
      Condition.have(Condition.text("text")));
    assertThat(condition)
      .hasToString("selected with text");
    assertThat(condition.apply(element))
      .isFalse();
    assertThat(condition)
      .hasToString("be selected");
  }

  @Test
  void elementOrCondition() {
    WebElement element = elementWithSelectedAndText(false, "text");
    when(element.isDisplayed()).thenReturn(true);
    assertThat(Condition.or("Visible, not Selected", Condition.visible, Condition.checked).apply(element))
      .isTrue();
    assertThat(Condition.or("Selected with text", Condition.checked, Condition.text("incorrect")).apply(element))
      .isFalse();
  }

  @Test
  void elementOrConditionActualValue() {
    WebElement element = elementWithSelectedAndText(false, "text");
    Condition condition = Condition.or("selected with text", be(Condition.selected),
      Condition.have(Condition.text("text")));
    assertThat(condition.actualValue(element))
      .isNullOrEmpty();
    assertThat(condition.apply(element))
      .isTrue();
    assertThat(condition.actualValue(element))
      .isEqualTo("false");
  }

  @Test
  void elementOrConditionToString() {
    WebElement element = elementWithSelectedAndText(false, "text");
    Condition condition = Condition.or("selected with text", be(Condition.selected),
      Condition.have(Condition.text("text")));
    assertThat(condition)
      .hasToString("selected with text");
    assertThat(condition.apply(element))
      .isTrue();
    assertThat(condition)
      .hasToString("be selected");
  }

  @Test
  void conditionBe() {
    Condition condition = be(Condition.visible);
    assertThat(condition)
      .hasToString("be visible");
  }

  @Test
  void conditionHave() {
    Condition condition = Condition.have(Condition.attribute("name"));
    assertThat(condition)
      .hasToString("have attribute name");
  }

  @Test
  void conditionApplyNull() {
    Condition condition = Condition.attribute("name");
    assertThat(condition.applyNull())
      .isFalse();
  }

  @Test
  void conditionToString() {
    Condition condition = Condition.attribute("name").because("it's awesome");
    assertThat(condition)
      .hasToString("attribute name (because it's awesome)");
  }
}
