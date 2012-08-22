package com.codeborne.selenide;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConditionTest {
  @Test
  public void displaysHumanReadableName() {
    assertEquals("become visible", Condition.visible.toString());
    assertEquals("become hidden", Condition.hidden.toString());
    assertEquals("got attribute lastName=Malkovich", Condition.hasAttribute("lastName", "Malkovich").toString());
  }
}
