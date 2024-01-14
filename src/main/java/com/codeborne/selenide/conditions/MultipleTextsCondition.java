package com.codeborne.selenide.conditions;

import java.util.Collection;

import static com.codeborne.selenide.conditions.MultipleTextsCondition.BlankPolicy.BLANK_FORBIDDEN;
import static java.util.Objects.isNull;

abstract class MultipleTextsCondition extends TextCondition {
  protected final Collection<String> targets;

  protected MultipleTextsCondition(String name, Collection<String> targets, BlankPolicy blankPolicy) {
    super(name, targets.toString());
    if (targets.isEmpty()) {
      throw new IllegalArgumentException("No expected texts given");
    }
    if (targets.stream().anyMatch(target -> isNull(target))) {
      throw new IllegalArgumentException("The expected texts should not contain null");
    }
    if (blankPolicy == BLANK_FORBIDDEN && targets.stream().anyMatch(target -> target.isBlank())) {
      throw new IllegalArgumentException("The expected texts should not contain blank strings");
    }
    this.targets = targets;
  }

  protected enum BlankPolicy {
    BLANK_ALLOWED,
    BLANK_FORBIDDEN
  }
}
