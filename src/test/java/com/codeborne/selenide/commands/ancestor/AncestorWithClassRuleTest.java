package com.codeborne.selenide.commands.ancestor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AncestorWithClassRuleTest {
  private final AncestorWithClassRule rule = new AncestorWithClassRule();

  @Test
  void isCssClass() {
    assertThat(rule.isCssClass(".active")).isTrue();
    assertThat(rule.isCssClass("div")).isFalse();
  }

  @Test
  void singleClassName() {
    assertThat(rule.evaluate(".active", 3)).contains(
      new AncestorResult("ancestor::*[ contains(concat(' ', normalize-space(@class), ' '), ' active ') ][3]"));
  }

  @Test
  void twoClassNames() {
    assertThat(rule.evaluate(".active.button", 3)).contains(
      new AncestorResult("ancestor::*[ " +
        "contains(concat(' ', normalize-space(@class), ' '), ' active ') " +
        "and " +
        "contains(concat(' ', normalize-space(@class), ' '), ' button ') " +
        "][3]"));
  }
}
