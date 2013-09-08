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
    when(element.getText()).thenReturn("John Malcovich The First");
    assertTrue(Condition.text("john malcovich").apply(element));
  }

  @Test
  public void textCaseSensitive() {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn("John Malcovich The First");
    assertFalse(Condition.textCaseSensitive("john malcovich").apply(element));
    assertTrue(Condition.textCaseSensitive("John Malcovich").apply(element));
  }

  @Test
  public void exactTextIsCaseInsensitive() {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn("John Malcovich");
    assertTrue(Condition.exactText("john malcovich").apply(element));
    assertFalse(Condition.exactText("john").apply(element));
  }

  @Test
  public void exactTextCaseSensitive() {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn("John Malcovich");
    assertFalse(Condition.exactTextCaseSensitive("john malcovich").apply(element));
    assertTrue(Condition.exactTextCaseSensitive("John Malcovich").apply(element));
    assertFalse(Condition.exactText("John").apply(element));
  }
}
