package com.codeborne.selenide;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConditionTest {
  @Test
  public void displaysHumanReadableName() {
    assertEquals("become visible", Condition.visible.toString());
    assertEquals("become hidden", Condition.hidden.toString());
    assertEquals("got attribute lastName=Malkovich", Condition.hasAttribute("lastName", "Malkovich").toString());
  }

  @Test
  public void textConditionIsCaseInsensitive() throws Exception {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn("John Malcovich The First");
    assertTrue(Condition.text("john malcovich").apply(element));
    assertTrue(Condition.haveText("john malcovich").apply(element));
    assertTrue(Condition.haveText("john malcovich").apply(element));
  }

  @Test
  public void textCaseSensitive() throws Exception {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn("John Malcovich The First");
    assertFalse(Condition.textCaseSensitive("john malcovich").apply(element));
    assertTrue(Condition.textCaseSensitive("John Malcovich").apply(element));
  }

  @Test
  public void exactTextIsCaseInsensitive() throws Exception {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn("John Malcovich");
    assertTrue(Condition.exactText("john malcovich").apply(element));
    assertFalse(Condition.exactText("john").apply(element));
  }

  @Test
  public void exactTextCaseSensitive() throws Exception {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn("John Malcovich");
    assertFalse(Condition.exactTextCaseSensitive("john malcovich").apply(element));
    assertTrue(Condition.exactTextCaseSensitive("John Malcovich").apply(element));
    assertFalse(Condition.exactText("John").apply(element));
  }
}
