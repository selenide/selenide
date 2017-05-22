package com.codeborne.selenide;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConditionTest {
  @Test
  public void displaysHumanReadableName() {
    assertEquals("visible", Condition.visible.toString());
    assertEquals("hidden", Condition.hidden.toString());
    assertEquals("attribute lastName=Malkovich", Condition.hasAttribute("lastName", "Malkovich").toString());
  }

  @Test
  public void textConditionIsCaseInsensitive() {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn("John Malkovich The First");
    assertTrue(Condition.text("john malkovich").apply(element));
  }

  @Test
  public void textConditionIgnoresWhitespaces() {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn("John  the\n Malkovich");
    assertTrue(Condition.text("john the malkovich").apply(element));

    when(element.getText()).thenReturn("This is nonbreakable\u00a0space");
    assertTrue(Condition.text("This is nonbreakable space").apply(element));
  }

  @Test
  public void textCaseSensitive() {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn("John Malkovich The First");
    assertFalse(Condition.textCaseSensitive("john malkovich").apply(element));
    assertTrue(Condition.textCaseSensitive("John Malkovich").apply(element));
  }

  @Test
  public void textCaseSensitiveIgnoresWhitespaces() {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn("John Malkovich\t The   \n First");
    assertFalse(Condition.textCaseSensitive("john malkovich").apply(element));
    assertTrue(Condition.textCaseSensitive("John        Malkovich The   ").apply(element));
  }

  @Test
  public void exactTextIsCaseInsensitive() {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn("John Malkovich");
    assertTrue(Condition.exactText("john malkovich").apply(element));
    assertFalse(Condition.exactText("john").apply(element));
  }

  @Test
  public void exactTextCaseSensitive() {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn("John Malkovich");
    assertFalse(Condition.exactTextCaseSensitive("john malkovich").apply(element));
    assertTrue(Condition.exactTextCaseSensitive("John Malkovich").apply(element));
    assertFalse(Condition.exactTextCaseSensitive("John").apply(element));
  }

  @Test
  public void value() {
    WebElement element = mock(WebElement.class);
    when(element.getAttribute("value")).thenReturn("John Malkovich");
    assertFalse(Condition.value("Peter").apply(element));
    assertTrue(Condition.value("john").apply(element));
    assertTrue(Condition.value("john malkovich").apply(element));
    assertTrue(Condition.value("John").apply(element));
    assertTrue(Condition.value("John Malkovich").apply(element));
  }

  @Test
  public void elementIsVisible() {
    WebElement element = mock(WebElement.class);
    when(element.isDisplayed()).thenReturn(true);
    assertTrue(Condition.visible.apply(element));
  }

  @Test
  public void elementExists() {
    WebElement element = mock(WebElement.class);
    when(element.isDisplayed()).thenReturn(true);
    assertTrue(Condition.exist.apply(element));
  }

  @Test
  public void elementIsHidden() {
    WebElement element = mock(WebElement.class);
    when(element.isDisplayed()).thenReturn(false);
    assertTrue(Condition.hidden.apply(element));
  }

  @Test
  public void elementHasAttribute() {
    WebElement element = mock(WebElement.class);
    when(element.getAttribute("name")).thenReturn("selenide");
    assertTrue(Condition.attribute("name").apply(element));
  }

  @Test
  public void elementHasAttributeWithGivenValue() {
    WebElement element = mock(WebElement.class);
    when(element.getAttribute("name")).thenReturn("selenide");
    assertTrue(Condition.attribute("name", "selenide").apply(element));
  }

  @Test
  public void elementHasValue() {
    WebElement element = mock(WebElement.class);
    when(element.getAttribute("value")).thenReturn("selenide");
    assertTrue(Condition.value("selenide").apply(element));
  }

  @Test
  public void elementHasValueMethod() {
    WebElement element = mock(WebElement.class);
    when(element.getAttribute("value")).thenReturn("selenide");
    assertTrue(Condition.hasValue("selenide").apply(element));
  }

  @Test
  public void elementHasName() {
    WebElement element = mock(WebElement.class);
    when(element.getAttribute("name")).thenReturn("selenide");
    assertTrue(Condition.name("selenide").apply(element));
  }

  @Test
  public void elementHasType() {
    WebElement element = mock(WebElement.class);
    when(element.getAttribute("type")).thenReturn("selenide");
    assertTrue(Condition.type("selenide").apply(element));
  }

  @Test
  public void elementHasId() {
    WebElement element = mock(WebElement.class);
    when(element.getAttribute("id")).thenReturn("selenide");
    assertTrue(Condition.id("selenide").apply(element));
  }

  @Test
  public void elementMatchesText() {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn("selenidehello");
    assertTrue(Condition.matchesText("selenide").apply(element));
  }

  @Test
  public void elementHasText() {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn("selenidehello");
    assertTrue(Condition.hasText("selenide").apply(element));
  }

  @Test
  public void elementHasClass() {
    WebElement element = mock(WebElement.class);
    when(element.getAttribute("class")).thenReturn("selenide");
    assertTrue(Condition.hasClass("selenide").apply(element));
  }

  @Test
  public void elementHasClassForElement() {
    WebElement element = mock(WebElement.class);
    when(element.getAttribute("class")).thenReturn("selenide");
    assertTrue(Condition.hasClass(element, "selenide"));
  }

  @Test
  public void elementHasCssClass() {
    WebElement element = mock(WebElement.class);
    when(element.getAttribute("class")).thenReturn("selenide");
    assertTrue(Condition.hasClass(element, "selenide"));
  }

  @Test
  public void elementEnabled() {
    WebElement element = mock(WebElement.class);
    when(element.isEnabled()).thenReturn(true);
    assertTrue(Condition.enabled.apply(element));
  }

  @Test
  public void elementDisabled() {
    WebElement element = mock(WebElement.class);
    when(element.isEnabled()).thenReturn(false);
    assertTrue(Condition.disabled.apply(element));
  }

  @Test
  public void elementSelected() {
    WebElement element = mock(WebElement.class);
    when(element.isSelected()).thenReturn(true);
    assertTrue(Condition.selected.apply(element));
  }

  @Test
  public void elementNotSelected() {
    WebElement element = mock(WebElement.class);
    when(element.isSelected()).thenReturn(false);
    assertFalse(Condition.selected.apply(element));
  }

  @Test
  public void elementChecked() {
    WebElement element = mock(WebElement.class);
    when(element.isSelected()).thenReturn(true);
    assertTrue(Condition.checked.apply(element));
  }


  @Test
  public void elementNotChecked() {
    WebElement element = mock(WebElement.class);
    when(element.isSelected()).thenReturn(false);
    assertFalse(Condition.checked.apply(element));
  }

  @Test
  public void elementNotCondition() {
    WebElement element = mock(WebElement.class);
    when(element.isSelected()).thenReturn(false);
    assertTrue(Condition.not(Condition.checked).apply(element));
  }

  @Test
  public void elementVisibleButNotSelected() {
    WebElement element = mock(WebElement.class);
    when(element.isDisplayed()).thenReturn(true);
    when(element.isSelected()).thenReturn(false);
    assertTrue(Condition.or("Visible, not Selected",
        Condition.visible,
        Condition.checked).apply(element));
  }

  @Test
  public void conditionBe() {
    Condition condition = Condition.be(Condition.visible);
    assertEquals("be visible", condition.toString());
  }

  @Test
  public void conditionHave() {
    Condition condition = Condition.have(Condition.attribute("name"));
    assertEquals("have attribute name", condition.toString());
  }

  @Test
  public void conditionApplyNull() {
    Condition condition = Condition.attribute("name");
    assertFalse(condition.applyNull());
  }

  @Test
  public void condtionToString() {
    Condition condition = Condition.attribute("name").because("it's awesome");
    assertEquals("attribute name (because it's awesome)", condition.toString());
  }


}
