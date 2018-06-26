package com.codeborne.selenide;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.be;
import static com.codeborne.selenide.Condition.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConditionTest {
  @Test
  void displaysHumanReadableName() {
    assertEquals("visible", Condition.visible.toString());
    assertEquals("hidden", Condition.hidden.toString());
    assertEquals("attribute lastName=Malkovich", Condition.hasAttribute("lastName", "Malkovich").toString());
  }

  @Test
  void textConditionChecksForSubstring() {
    assertTrue(Condition.text("John Malkovich The First").apply(elementWithText("John Malkovich The First")));
    assertFalse(Condition.text("John Malkovich First").apply(elementWithText("John Malkovich The First")));
    assertFalse(Condition.text("john bon jovi").apply(elementWithText("John Malkovich The First")));
  }

  private WebElement elementWithText(String text) {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn(text);
    return element;
  }

  @Test
  void textConditionIsCaseInsensitive() {
    WebElement element = elementWithText("John Malkovich The First");
    assertTrue(Condition.text("john malkovich").apply(element));
  }

  @Test
  void textConditionIgnoresWhitespaces() {
    assertTrue(Condition.text("john the malkovich").apply(
      elementWithText("John  the\n Malkovich")));
    assertTrue(Condition.text("This is nonbreakable space").apply(
      elementWithText("This is nonbreakable\u00a0space")));
  }

  @Test
  void textCaseSensitive() {
    WebElement element = elementWithText("John Malkovich The First");
    assertFalse(Condition.textCaseSensitive("john malkovich").apply(element));
    assertTrue(Condition.textCaseSensitive("John Malkovich").apply(element));
  }

  @Test
  void textCaseSensitiveIgnoresWhitespaces() {
    WebElement element = elementWithText("John Malkovich\t The   \n First");
    assertFalse(Condition.textCaseSensitive("john malkovich").apply(element));
    assertTrue(Condition.textCaseSensitive("John        Malkovich The   ").apply(element));
  }

  @Test
  void textCaseSencitiveToString() {
    assertEquals("textCaseSensitive 'John Malcovich'", Condition.textCaseSensitive("John Malcovich").toString());
  }

  @Test
  void exactTextIsCaseInsensitive() {
    WebElement element = elementWithText("John Malkovich");
    assertTrue(Condition.exactText("john malkovich").apply(element));
    assertFalse(Condition.exactText("john").apply(element));
  }

  @Test
  void exactTextToString() {
    assertEquals("exact text 'John Malcovich'", Condition.exactText("John Malcovich").toString());
  }

  @Test
  void exactTextCaseSensitive() {
    WebElement element = elementWithText("John Malkovich");
    assertFalse(Condition.exactTextCaseSensitive("john malkovich").apply(element));
    assertTrue(Condition.exactTextCaseSensitive("John Malkovich").apply(element));
    assertFalse(Condition.exactTextCaseSensitive("John").apply(element));
  }

  @Test
  void exactTextCaseSensitiveToString() {
    assertEquals("exact text case sensitive 'John Malcovich'", Condition.exactTextCaseSensitive("John Malcovich").toString());
  }

  @Test
  void value() {
    WebElement element = elementWithAttribute("value", "John Malkovich");
    assertFalse(Condition.value("Peter").apply(element));
    assertTrue(Condition.value("john").apply(element));
    assertTrue(Condition.value("john malkovich").apply(element));
    assertTrue(Condition.value("John").apply(element));
    assertTrue(Condition.value("John Malkovich").apply(element));
    assertTrue(Condition.value("malko").apply(element));
  }

  private WebElement elementWithAttribute(String name, String value) {
    WebElement element = mock(WebElement.class);
    when(element.getAttribute(name)).thenReturn(value);
    return element;
  }

  @Test
  void valueToString() {
    assertEquals("value 'John Malkovich'", Condition.value("John Malkovich").toString());
  }

  @Test
  void elementIsVisible() {
    assertTrue(Condition.visible.apply(elementWithVisibility(true)));
    assertFalse(Condition.visible.apply(elementWithVisibility(false)));
  }

  private WebElement elementWithVisibility(boolean isVisible) {
    WebElement element = mock(WebElement.class);
    when(element.isDisplayed()).thenReturn(isVisible);
    return element;
  }

  @Test
  void elementExists() {
    assertTrue(Condition.exist.apply(elementWithVisibility(true)));
    assertTrue(Condition.exist.apply(elementWithVisibility(false)));
  }

  @Test
  void elementExists_returnsFalse_ifItThrowsException() {
    WebElement element = mock(WebElement.class);
    when(element.isDisplayed()).thenThrow(new StaleElementReferenceException("ups"));
    assertFalse(Condition.exist.apply(element));
  }

  @Test
  void elementIsHidden() {
    assertTrue(Condition.hidden.apply(elementWithVisibility(false)));
    assertFalse(Condition.hidden.apply(elementWithVisibility(true)));
  }

  @Test
  void elementIsHiddenWithStaleElementException() {
    WebElement element = mock(WebElement.class);
    doThrow(new StaleElementReferenceException("Oooops")).when(element).isDisplayed();
    assertTrue(Condition.hidden.apply(element));
  }

  @Test
  void elementHasAttribute() {
    assertTrue(Condition.attribute("name").apply(elementWithAttribute("name", "selenide")));
    assertTrue(Condition.attribute("name").apply(elementWithAttribute("name", "")));
    assertFalse(Condition.attribute("name").apply(elementWithAttribute("id", "id3")));
  }

  @Test
  void elementHasAttributeWithGivenValue() {
    assertTrue(Condition.attribute("name", "selenide").apply(elementWithAttribute("name", "selenide")));
    assertFalse(Condition.attribute("name", "selenide").apply(elementWithAttribute("name", "selenide is great")));
    assertFalse(Condition.attribute("name", "selenide").apply(elementWithAttribute("id", "id2")));
  }

  @Test
  void elementHasValue() {
    assertTrue(Condition.value("selenide").apply(elementWithAttribute("value", "selenide")));
    assertTrue(Condition.value("selenide").apply(elementWithAttribute("value", "selenide is great")));
    assertFalse(Condition.value("selenide").apply(elementWithAttribute("value", "is great")));
  }

  @Test
  void elementHasValueMethod() {
    assertTrue(Condition.hasValue("selenide").apply(elementWithAttribute("value", "selenide")));
    assertTrue(Condition.hasValue("selenide").apply(elementWithAttribute("value", "selenide is great")));
    assertFalse(Condition.hasValue("selenide").apply(elementWithAttribute("value", "is great")));
  }

  @Test
  void elementHasName() {
    assertTrue(Condition.name("selenide").apply(elementWithAttribute("name", "selenide")));
    assertFalse(Condition.name("selenide").apply(elementWithAttribute("name", "selenide is great")));
  }

  @Test
  void elementHasType() {
    assertTrue(Condition.type("selenide").apply(elementWithAttribute("type", "selenide")));
    assertFalse(Condition.type("selenide").apply(elementWithAttribute("type", "selenide is great")));
  }

  @Test
  void elementHasId() {
    assertTrue(Condition.id("selenide").apply(elementWithAttribute("id", "selenide")));
    assertFalse(Condition.id("selenide").apply(elementWithAttribute("id", "selenide is great")));
  }

  @Test
  void elementMatchesText() {
    assertTrue(Condition.matchesText("selenide").apply(elementWithText("selenidehello")));
    assertTrue(Condition.matchesText("selenide").apply(elementWithText("  this is  selenide  the great ")));
    assertTrue(Condition.matchesText("selenide\\s+hello\\s*").apply(elementWithText("selenide    hello")));
    assertFalse(Condition.matchesText("selenide").apply(elementWithText("selenite")));
  }

  @Test
  void elementMatchTextToString() {
    assertEquals("match text 'John Malcovich'", Condition.matchesText("John Malcovich").toString());
  }

  @Test
  void elementHasText() {
    assertTrue(Condition.hasText("selenide").apply(elementWithText("selenidehello")));
    assertTrue(Condition.hasText("hello").apply(elementWithText("selenidehello")));
    assertFalse(Condition.hasText("selenide, hello").apply(elementWithText("selenidehello")));
  }

  @Test
  void elementHasClass() {
    assertTrue(Condition.hasClass("btn").apply(elementWithAttribute("class", "btn btn-warning")));
    assertTrue(Condition.hasClass("btn-warning").apply(elementWithAttribute("class", "btn btn-warning")));
    assertFalse(Condition.hasClass("active").apply(elementWithAttribute("class", "btn btn-warning")));
  }

  @Test
  void elementHasCssValue() {
    assertTrue(Condition.cssValue("display", "none").apply(elementWithCssStyle("display", "none")));
    assertFalse(Condition.cssValue("font-size", "24").apply(elementWithCssStyle("font-size", "20")));
  }

  private WebElement elementWithCssStyle(String propertyName, String value) {
    WebElement element = mock(WebElement.class);
    when(element.getCssValue(propertyName)).thenReturn(value);
    return element;
  }

  @Test
  void elementHasClassToString() {
    assertEquals("css class 'Foo'", Condition.hasClass("Foo").toString());
  }

  @Test
  void elementHasClassForElement() {
    assertTrue(Condition.hasClass(elementWithAttribute("class", "btn btn-warning"), "btn"));
    assertTrue(Condition.hasClass(elementWithAttribute("class", "btn btn-warning"), "btn-warning"));
    assertFalse(Condition.hasClass(elementWithAttribute("class", "btn btn-warning"), "form-horizontal"));
  }

  @Test
  void elementEnabled() {
    assertTrue(Condition.enabled.apply(elementWithEnabled(true)));
    assertFalse(Condition.enabled.apply(elementWithEnabled(false)));
  }

  private WebElement elementWithEnabled(boolean isEnabled) {
    WebElement element = mock(WebElement.class);
    when(element.isEnabled()).thenReturn(isEnabled);
    return element;
  }

  @Test
  void elementEnabledActualValue() {
    assertEquals("enabled", Condition.enabled.actualValue(elementWithEnabled(true)));
    assertEquals("disabled", Condition.enabled.actualValue(elementWithEnabled(false)));
  }

  @Test
  void elementDisabled() {
    assertTrue(Condition.disabled.apply(elementWithEnabled(false)));
    assertFalse(Condition.disabled.apply(elementWithEnabled(true)));
  }

  @Test
  void elementDisabledActualValue() {
    assertEquals("enabled", Condition.disabled.actualValue(elementWithEnabled(true)));
    assertEquals("disabled", Condition.disabled.actualValue(elementWithEnabled(false)));
  }

  @Test
  void elementSelected() {
    assertTrue(Condition.selected.apply(elementWithSelected(true)));
    assertFalse(Condition.selected.apply(elementWithSelected(false)));
  }

  private WebElement elementWithSelected(boolean isSelected) {
    WebElement element = mock(WebElement.class);
    when(element.isSelected()).thenReturn(isSelected);
    return element;
  }

  @Test
  void elementSelectedActualValue() {
    assertEquals("true", Condition.selected.actualValue(elementWithSelected(true)));
    assertEquals("false", Condition.selected.actualValue(elementWithSelected(false)));
  }

  @Test
  void elementChecked() {
    assertTrue(Condition.checked.apply(elementWithSelected(true)));
    assertFalse(Condition.checked.apply(elementWithSelected(false)));
  }

  @Test
  void elementCheckedActualValue() {
    assertEquals("true", Condition.checked.actualValue(elementWithSelected(true)));
    assertEquals("false", Condition.checked.actualValue(elementWithSelected(false)));
  }

  @Test
  void elementNotCondition() {
    assertTrue(not(Condition.checked).apply(elementWithSelected(false)));
    assertFalse(not(Condition.checked).apply(elementWithSelected(true)));
  }

  @Test
  void elementNotCondtionActualValue() {
    assertEquals("false", not(Condition.checked).actualValue(elementWithSelected(false)));
    assertEquals("true", not(Condition.checked).actualValue(elementWithSelected(true)));
  }

  @Test
  void elementAndCondition() {
    WebElement element = elementWithSelectedAndText(true, "text");
    assertTrue(Condition.and("selected with text", be(Condition.selected),
      Condition.have(Condition.text("text"))).apply(element));
    assertFalse(Condition.and("selected with text", not(be(Condition.selected)),
      Condition.have(Condition.text("text"))).apply(element));
    assertFalse(Condition.and("selected with text", be(Condition.selected),
      Condition.have(Condition.text("incorrect"))).apply(element));
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
    assertNull(condition.actualValue(element));
    assertFalse(condition.apply(element));
    assertEquals("false", condition.actualValue(element));
  }

  @Test
  void elementAndConditionToString() {
    WebElement element = elementWithSelectedAndText(false, "text");
    Condition condition = Condition.and("selected with text", be(Condition.selected),
      Condition.have(Condition.text("text")));
    assertEquals("selected with text", condition.toString());
    assertFalse(condition.apply(element));
    assertEquals("be selected", condition.toString());
  }

  @Test
  void elementOrCondition() {
    WebElement element = elementWithSelectedAndText(false, "text");
    when(element.isDisplayed()).thenReturn(true);
    assertTrue(Condition.or("Visible, not Selected",
      Condition.visible,
      Condition.checked).apply(element));
    assertFalse(Condition.or("Selected with text",
      Condition.checked,
      Condition.text("incorrect")).apply(element));
  }

  @Test
  void elementOrConditionActualValue() {
    WebElement element = elementWithSelectedAndText(false, "text");
    Condition condition = Condition.or("selected with text", be(Condition.selected),
      Condition.have(Condition.text("text")));
    assertNull(condition.actualValue(element));
    assertTrue(condition.apply(element));
    assertEquals("false", condition.actualValue(element));
  }

  @Test
  void elementOrConditionToString() {
    WebElement element = elementWithSelectedAndText(false, "text");
    Condition condition = Condition.or("selected with text", be(Condition.selected),
      Condition.have(Condition.text("text")));
    assertEquals("selected with text", condition.toString());
    assertTrue(condition.apply(element));
    assertEquals("be selected", condition.toString());
  }

  @Test
  void conditionBe() {
    Condition condition = be(Condition.visible);
    assertEquals("be visible", condition.toString());
  }

  @Test
  void conditionHave() {
    Condition condition = Condition.have(Condition.attribute("name"));
    assertEquals("have attribute name", condition.toString());
  }

  @Test
  void conditionApplyNull() {
    Condition condition = Condition.attribute("name");
    assertFalse(condition.applyNull());
  }

  @Test
  void conditionToString() {
    Condition condition = Condition.attribute("name").because("it's awesome");
    assertEquals("attribute name (because it's awesome)", condition.toString());
  }
}
