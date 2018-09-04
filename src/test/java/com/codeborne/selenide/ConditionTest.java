package com.codeborne.selenide;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.and;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.be;
import static com.codeborne.selenide.Condition.checked;
import static com.codeborne.selenide.Condition.cssValue;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactTextCaseSensitive;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.hasAttribute;
import static com.codeborne.selenide.Condition.hasClass;
import static com.codeborne.selenide.Condition.hasText;
import static com.codeborne.selenide.Condition.hasValue;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConditionTest {
  private WebDriver webDriver = mock(WebDriver.class);
  private SelenideProxyServer proxy = mock(SelenideProxyServer.class);
  private Context context = new ContextStub(new Browser("opera", false), webDriver, proxy);
  
  @Test
  void displaysHumanReadableName() {
    assertThat(visible).hasToString("visible");
    assertThat(hidden).hasToString("hidden");
    assertThat(hasAttribute("lastName", "Malkovich")).hasToString("attribute lastName=Malkovich");
  }

  @Test
  void textConditionChecksForSubstring() {
    assertThat(text("John Malkovich The First").apply(context, elementWithText("John Malkovich The First")))
      .isTrue();
    assertThat(text("John Malkovich First").apply(context, elementWithText("John Malkovich The First")))
      .isFalse();
    assertThat(text("john bon jovi").apply(context, elementWithText("John Malkovich The First")))
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
    assertThat(text("john malkovich").apply(context, element)).isTrue();
  }

  @Test
  void textConditionIgnoresWhitespaces() {
    assertThat(text("john the malkovich").apply(context, elementWithText("John  the\n Malkovich")))
      .isTrue();
    assertThat(text("This is nonbreakable space").apply(context, elementWithText("This is nonbreakable\u00a0space")))
      .isTrue();
  }

  @Test
  void testTextCaseSensitive() {
    WebElement element = elementWithText("John Malkovich The First");
    assertThat(textCaseSensitive("john malkovich").apply(context, element)).isFalse();
    assertThat(textCaseSensitive("John Malkovich").apply(context, element)).isTrue();
  }

  @Test
  void textCaseSensitiveIgnoresWhitespaces() {
    WebElement element = elementWithText("John Malkovich\t The   \n First");
    assertThat(textCaseSensitive("john malkovich").apply(context, element)).isFalse();
    assertThat(textCaseSensitive("John        Malkovich The   ").apply(context, element)).isTrue();
  }

  @Test
  void textCaseSensitiveToString() {
    assertThat(textCaseSensitive("John Malcovich")).hasToString("textCaseSensitive 'John Malcovich'");
  }

  @Test
  void exactTextIsCaseInsensitive() {
    WebElement element = elementWithText("John Malkovich");
    assertThat(exactText("john malkovich").apply(context, element)).isTrue();
    assertThat(exactText("john").apply(context, element)).isFalse();
  }

  @Test
  void exactTextToString() {
    assertThat(exactText("John Malcovich")).hasToString("exact text 'John Malcovich'");
  }

  @Test
  void testExactTextCaseSensitive() {
    WebElement element = elementWithText("John Malkovich");
    assertThat(exactTextCaseSensitive("john malkovich").apply(context, element)).isFalse();
    assertThat(exactTextCaseSensitive("John Malkovich").apply(context, element)).isTrue();
    assertThat(exactTextCaseSensitive("John").apply(context, element)).isFalse();
  }

  @Test
  void exactTextCaseSensitiveToString() {
    assertThat(exactTextCaseSensitive("John Malcovich"))
      .hasToString("exact text case sensitive 'John Malcovich'");
  }

  @Test
  void value() {
    WebElement element = elementWithAttribute("value", "John Malkovich");
    assertThat(Condition.value("Peter").apply(context, element)).isFalse();
    assertThat(Condition.value("john").apply(context, element)).isTrue();
    assertThat(Condition.value("john malkovich").apply(context, element)).isTrue();
    assertThat(Condition.value("John").apply(context, element)).isTrue();
    assertThat(Condition.value("John Malkovich").apply(context, element)).isTrue();
    assertThat(Condition.value("malko").apply(context, element)).isTrue();
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
    assertThat(visible.apply(context, elementWithVisibility(true))).isTrue();
    assertThat(visible.apply(context, elementWithVisibility(false))).isFalse();
  }

  private WebElement elementWithVisibility(boolean isVisible) {
    WebElement element = mock(WebElement.class);
    when(element.isDisplayed()).thenReturn(isVisible);
    return element;
  }

  @Test
  void elementExists() {
    assertThat(exist.apply(context, elementWithVisibility(true))).isTrue();
    assertThat(exist.apply(context, elementWithVisibility(false))).isTrue();
  }

  @Test
  void elementExists_returnsFalse_ifItThrowsException() {
    WebElement element = mock(WebElement.class);
    when(element.isDisplayed()).thenThrow(new StaleElementReferenceException("ups"));
    assertThat(exist.apply(context, element)).isFalse();
  }

  @Test
  void elementIsHidden() {
    assertThat(hidden.apply(context, elementWithVisibility(false))).isTrue();
    assertThat(hidden.apply(context, elementWithVisibility(true))).isFalse();
  }

  @Test
  void elementIsHiddenWithStaleElementException() {
    WebElement element = mock(WebElement.class);
    doThrow(new StaleElementReferenceException("Oooops")).when(element).isDisplayed();
    assertThat(hidden.apply(context, element)).isTrue();
  }

  @Test
  void elementHasAttribute() {
    assertThat(attribute("name").apply(context, elementWithAttribute("name", "selenide"))).isTrue();
    assertThat(attribute("name").apply(context, elementWithAttribute("name", ""))).isTrue();
    assertThat(attribute("name").apply(context, elementWithAttribute("id", "id3"))).isFalse();
  }

  @Test
  void elementHasAttributeWithGivenValue() {
    assertThat(attribute("name", "selenide").apply(context, elementWithAttribute("name", "selenide"))).isTrue();
    assertThat(attribute("name", "selenide").apply(context, elementWithAttribute("name", "selenide is great"))).isFalse();
    assertThat(attribute("name", "selenide").apply(context, elementWithAttribute("id", "id2"))).isFalse();
  }

  @Test
  void elementHasValue() {
    assertThat(Condition.value("selenide").apply(context, elementWithAttribute("value", "selenide"))).isTrue();
    assertThat(Condition.value("selenide").apply(context, elementWithAttribute("value", "selenide is great"))).isTrue();
    assertThat(Condition.value("selenide").apply(context, elementWithAttribute("value", "is great"))).isFalse();
  }

  @Test
  void elementHasValueMethod() {
    assertThat(hasValue("selenide").apply(context, elementWithAttribute("value", "selenide"))).isTrue();
    assertThat(hasValue("selenide").apply(context, elementWithAttribute("value", "selenide is great"))).isTrue();
    assertThat(hasValue("selenide").apply(context, elementWithAttribute("value", "is great"))).isFalse();
  }

  @Test
  void elementHasName() {
    assertThat(name("selenide").apply(context, elementWithAttribute("name", "selenide"))).isTrue();
    assertThat(name("selenide").apply(context, elementWithAttribute("name", "selenide is great"))).isFalse();
  }

  @Test
  void elementHasType() {
    assertThat(type("selenide").apply(context, elementWithAttribute("type", "selenide"))).isTrue();
    assertThat(type("selenide").apply(context, elementWithAttribute("type", "selenide is great"))).isFalse();
  }

  @Test
  void elementHasId() {
    assertThat(id("selenide").apply(context, elementWithAttribute("id", "selenide"))).isTrue();
    assertThat(id("selenide").apply(context, elementWithAttribute("id", "selenide is great"))).isFalse();
  }

  @Test
  void elementMatchesText() {
    assertThat(matchesText("selenide").apply(context, elementWithText("selenidehello"))).isTrue();
    assertThat(matchesText("selenide").apply(context, elementWithText("  this is  selenide  the great "))).isTrue();
    assertThat(matchesText("selenide\\s+hello\\s*").apply(context, elementWithText("selenide    hello"))).isTrue();
    assertThat(matchesText("selenide").apply(context, elementWithText("selenite"))).isFalse();
  }

  @Test
  void elementMatchTextToString() {
    assertThat(matchesText("John Malcovich")).hasToString("match text 'John Malcovich'");
  }

  @Test
  void elementHasText() {
    assertThat(hasText("selenide").apply(context, elementWithText("selenidehello"))).isTrue();
    assertThat(hasText("hello").apply(context, elementWithText("selenidehello"))).isTrue();
    assertThat(hasText("selenide, hello").apply(context, elementWithText("selenidehello"))).isFalse();
  }

  @Test
  void elementHasClass() {
    assertThat(hasClass("btn").apply(context, elementWithAttribute("class", "btn btn-warning"))).isTrue();
    assertThat(hasClass("btn-warning").apply(context, elementWithAttribute("class", "btn btn-warning"))).isTrue();
    assertThat(hasClass("active").apply(context, elementWithAttribute("class", "btn btn-warning"))).isFalse();
  }

  @Test
  void elementHasCssValue() {
    assertThat(cssValue("display", "none").apply(context, elementWithCssStyle("display", "none"))).isTrue();
    assertThat(cssValue("font-size", "24").apply(context, elementWithCssStyle("font-size", "20"))).isFalse();
  }

  private WebElement elementWithCssStyle(String propertyName, String value) {
    WebElement element = mock(WebElement.class);
    when(element.getCssValue(propertyName)).thenReturn(value);
    return element;
  }

  @Test
  void elementHasClassToString() {
    assertThat(hasClass("Foo")).hasToString("css class 'Foo'");
  }

  @Test
  void elementHasClassForElement() {
    assertThat(hasClass(elementWithAttribute("class", "btn btn-warning"), "btn")).isTrue();
    assertThat(hasClass(elementWithAttribute("class", "btn btn-warning"), "btn-warning")).isTrue();
    assertThat(hasClass(elementWithAttribute("class", "btn btn-warning"), "form-horizontal")).isFalse();
  }

  @Test
  void elementEnabled() {
    assertThat(enabled.apply(context, elementWithEnabled(true))).isTrue();
    assertThat(enabled.apply(context, elementWithEnabled(false))).isFalse();
  }

  private WebElement elementWithEnabled(boolean isEnabled) {
    WebElement element = mock(WebElement.class);
    when(element.isEnabled()).thenReturn(isEnabled);
    return element;
  }

  @Test
  void elementEnabledActualValue() {
    assertThat(enabled.actualValue(context, elementWithEnabled(true))).isEqualTo("enabled");
    assertThat(enabled.actualValue(context, elementWithEnabled(false))).isEqualTo("disabled");
  }

  @Test
  void elementDisabled() {
    assertThat(disabled.apply(context, elementWithEnabled(false))).isTrue();
    assertThat(disabled.apply(context, elementWithEnabled(true))).isFalse();
  }

  @Test
  void elementDisabledActualValue() {
    assertThat(disabled.actualValue(context, elementWithEnabled(true))).isEqualTo("enabled");
    assertThat(disabled.actualValue(context, elementWithEnabled(false))).isEqualTo("disabled");
  }

  @Test
  void elementSelected() {
    assertThat(selected.apply(context, elementWithSelected(true))).isTrue();
    assertThat(selected.apply(context, elementWithSelected(false))).isFalse();
  }

  private WebElement elementWithSelected(boolean isSelected) {
    WebElement element = mock(WebElement.class);
    when(element.isSelected()).thenReturn(isSelected);
    return element;
  }

  @Test
  void elementSelectedActualValue() {
    assertThat(selected.actualValue(context, elementWithSelected(true))).isEqualTo("true");
    assertThat(selected.actualValue(context, elementWithSelected(false))).isEqualTo("false");
  }

  @Test
  void elementChecked() {
    assertThat(checked.apply(context, elementWithSelected(true))).isTrue();
    assertThat(checked.apply(context, elementWithSelected(false))).isFalse();
  }

  @Test
  void elementCheckedActualValue() {
    assertThat(checked.actualValue(context, elementWithSelected(true))).isEqualTo("true");
    assertThat(checked.actualValue(context, elementWithSelected(false))).isEqualTo("false");
  }

  @Test
  void elementNotCondition() {
    assertThat(not(checked).apply(context, elementWithSelected(false))).isTrue();
    assertThat(not(checked).apply(context, elementWithSelected(true))).isFalse();
  }

  @Test
  void elementNotConditionActualValue() {
    assertThat(not(checked).actualValue(context, elementWithSelected(false))).isEqualTo("false");
    assertThat(not(checked).actualValue(context, elementWithSelected(true))).isEqualTo("true");
  }

  @Test
  void elementAndCondition() {
    WebElement element = elementWithSelectedAndText(true, "text");
    assertThat(and("selected with text", be(selected), have(text("text"))).apply(context, element))
      .isTrue();
    assertThat(and("selected with text", not(be(selected)), have(text("text")))
      .apply(context, element))
      .isFalse();
    assertThat(and("selected with text", be(selected), have(text("incorrect"))).apply(context, element))
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
    Condition condition = and("selected with text", be(selected), have(text("text")));
    assertThat(condition.actualValue(context, element)).isNullOrEmpty();
    assertThat(condition.apply(context, element)).isFalse();
    assertThat(condition.actualValue(context, element)).isEqualTo("false");
  }

  @Test
  void elementAndConditionToString() {
    WebElement element = elementWithSelectedAndText(false, "text");
    Condition condition = and("selected with text", be(selected), have(text("text")));
    assertThat(condition).hasToString("selected with text");
    assertThat(condition.apply(context, element)).isFalse();
    assertThat(condition).hasToString("be selected");
  }

  @Test
  void elementOrCondition() {
    WebElement element = elementWithSelectedAndText(false, "text");
    when(element.isDisplayed()).thenReturn(true);
    assertThat(or("Visible, not Selected", visible, checked).apply(context, element)).isTrue();
    assertThat(or("Selected with text", checked, text("incorrect")).apply(context, element)).isFalse();
  }

  @Test
  void elementOrConditionActualValue() {
    WebElement element = elementWithSelectedAndText(false, "text");
    Condition condition = or("selected with text", be(selected), have(text("text")));
    assertThat(condition.actualValue(context, element)).isNullOrEmpty();
    assertThat(condition.apply(context, element)).isTrue();
    assertThat(condition.actualValue(context, element)).isEqualTo("false");
  }

  @Test
  void elementOrConditionToString() {
    WebElement element = elementWithSelectedAndText(false, "text");
    Condition condition = or("selected with text", be(selected), have(text("text")));
    assertThat(condition).hasToString("selected with text");
    assertThat(condition.apply(context, element)).isTrue();
    assertThat(condition).hasToString("be selected");
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
}
