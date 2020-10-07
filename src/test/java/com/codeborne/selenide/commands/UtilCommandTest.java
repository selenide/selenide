package com.codeborne.selenide.commands;

import java.util.List;

import com.codeborne.selenide.Condition;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;

final class UtilCommandTest implements WithAssertions {
  @Test
  void testArgsToCondition() {
    List<Condition> conditions = Util.argsToConditions(new Object[]{
      Condition.enabled,
      new Condition[]{Condition.appear, Condition.exist},
      "hello",
      2L
    });
    assertThat(conditions)
      .isEqualTo(asList(Condition.enabled, Condition.visible, Condition.exist));
  }

  @Test
  void testArgsToConditionWithIllegalArguments() {
    assertThatThrownBy(() -> {
      List<Condition> conditions = Util.argsToConditions(new Object[]{
        Condition.enabled,
        new Condition[]{Condition.appear, Condition.exist},
        1,
        2.0
      });
      assertThat(conditions)
        .isEqualTo(asList(Condition.enabled, Condition.visible, Condition.exist));
    }).isInstanceOf(IllegalArgumentException.class);
  }
}
