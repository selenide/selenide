package com.codeborne.selenide.commands.closest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClosestRuleEngine {

  private static final List<ClosestRule> rules = new ArrayList<>();

  static {
    rules.addAll(Arrays.asList(
      new ClosestWithTagRule(),
      new ClosestWithClassRule(),
      new ClosestWithAttributeRule(),
      new ClosestWithAttributeAndValueRule()
    ));
  }

  public ClosestResult process(String selector) {
    ClosestRule closestRule = rules
      .stream()
      .filter(rule -> rule.evaluate(selector))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Selector does not match any rule"));
    return closestRule.getClosestResult();
  }
}
