package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class UtilCommandTest {
  @Test
  public void testArgsToCondition() {
    List<Condition> conditions = Util.argsToConditions(new Object[]{
      Condition.enabled,
      new Condition[]{Condition.appear, Condition.exist},
      "hello",
      2L
    });
    assertEquals(asList(Condition.enabled, Condition.visible, Condition.exist), conditions);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testArgsToConditionWithIllegalArguments() {
    List<Condition> conditions = Util.argsToConditions(new Object[]{
      Condition.enabled,
      new Condition[]{Condition.appear, Condition.exist},
      1,
      2.0
    });
    assertEquals(asList(Condition.enabled, Condition.visible, Condition.exist), conditions);
  }
}
