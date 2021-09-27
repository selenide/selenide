package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.readonly;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.commands.Util.argsToConditions;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class UtilCommandTest {
  @Test
  void extractsConditionsFromGivenArguments() {
    List<Condition> conditions = argsToConditions(new Object[]{enabled, visible});
    assertThat(conditions).isEqualTo(asList(enabled, visible));
  }

  @Test
  void supportsArraysOfConditions() {
    List<Condition> conditions = argsToConditions(new Object[]{
      enabled,
      new Condition[]{appear, exist},
      new Condition[]{disabled, enabled, readonly}
    });
    assertThat(conditions).isEqualTo(asList(enabled, appear, exist, disabled, enabled, readonly));
  }

  @Test
  void ignores_String_Long_Duration_arguments() {
    List<Condition> conditions = argsToConditions(new Object[]{42L, "Some text", exist, Duration.ofSeconds(3), visible});
    assertThat(conditions).isEqualTo(asList(exist, visible));
  }

  @Test
  void doesNotAllowOtherTypesOfArguments() {
    assertThatThrownBy(() -> argsToConditions(new Object[]{enabled, 2}))
      .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> argsToConditions(new Object[]{enabled, 2.0}))
      .isInstanceOf(IllegalArgumentException.class);
  }
}
