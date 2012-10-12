package com.codeborne.selenide;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MatchTextConditionTest {
  @Test
  public void displaysHumanReadableName() {
    assertEquals("matched text 'abc'", Condition.matchText("abc").toString());
  }

  @Test
  public void matchesWholeString() {
    assertTrue(Condition.matchText("Chuck Norris' gmail account is gmail@chuck.norris").apply(element("Chuck Norris' gmail account is gmail@chuck.norris")));
    assertTrue(Condition.matchText("Chuck Norris.* gmail\\s+account is gmail@chuck.norris").apply(element("Chuck Norris' gmail    account is gmail@chuck.norris")));
  }

  @Test
  public void matchesSubstring() {
    assertTrue(Condition.matchText("Chuck").apply(element("Chuck Norris' gmail account is gmail@chuck.norris")));
    assertTrue(Condition.matchText("Chuck\\s*Norris").apply(element("Chuck Norris' gmail account is gmail@chuck.norris")));
    assertTrue(Condition.matchText("gmail account").apply(element("Chuck Norris' gmail account is gmail@chuck.norris")));
  }

  private WebElement element(String text) {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn(text);
    return element;
  }
}
