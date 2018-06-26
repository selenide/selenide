package com.codeborne.selenide;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MatchTextConditionTest {
  @Test
  void displaysHumanReadableName() {
    Assertions.assertEquals("match text 'abc'", Condition.matchText("abc").toString());
  }

  @Test
  void matchesWholeString() {
    Assertions.assertTrue(Condition.matchText("Chuck Norris' gmail account is gmail@chuck.norris")
      .apply(element("Chuck Norris' gmail account is gmail@chuck.norris")));

    Assertions.assertTrue(Condition.matchText("Chuck Norris.* gmail\\s+account is gmail@chuck.norris")
      .apply(element("Chuck Norris' gmail    account is gmail@chuck.norris")));
  }

  private WebElement element(String text) {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn(text);
    return element;
  }

  @Test
  void matchesSubstring() {
    Assertions.assertTrue(Condition.matchText("Chuck").apply(element("Chuck Norris' gmail account is gmail@chuck.norris")));
    Assertions.assertTrue(Condition.matchText("Chuck\\s*Norris").apply(element("Chuck Norris' gmail account is gmail@chuck.norris")));
    Assertions.assertTrue(Condition.matchText("gmail account").apply(element("Chuck Norris' gmail account is gmail@chuck.norris")));
  }
}
