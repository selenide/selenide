package com.codeborne.selenide.commands.ancestor;

import java.util.Arrays;
import java.util.List;

public class AncestorRuleEngine {

  private static final List<AncestorRule> rules = Arrays.asList(
    new AncestorWithTagRule(),
    new AncestorWithClassRule(),
    new AncestorWithAttributeRule(),
    new AncestorWithAttributeAndValueRule()
  );

  public AncestorResult process(String selector, int index) {
    AncestorRule ancestorRule = rules
      .stream()
      .filter(rule -> rule.evaluate(selector, index))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Selector does not match any rule"));
    return ancestorRule.getAncestorResult();
  }
}
