package com.codeborne.selenide.commands;

import java.util.List;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;

class UtilCommandTest {
  @Test
  void testArgsToCondition() {
    List<Condition> conditions = Util.argsToConditions(new Object[]{
      Condition.enabled,
      new Condition[]{Condition.appear, Condition.exist},
      "hello",
      2L
    });
    Assertions.assertEquals(asList(Condition.enabled, Condition.visible, Condition.exist), conditions);
  }

  @Test
  void testArgsToConditionWithIllegalArguments() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      List<Condition> conditions = Util.argsToConditions(new Object[]{
        Condition.enabled,
        new Condition[]{Condition.appear, Condition.exist},
        1,
        2.0
      });
      Assertions.assertEquals(asList(Condition.enabled, Condition.visible, Condition.exist), conditions);
    });
  }
}
