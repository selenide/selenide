package com.codeborne.selenide.commands.ancestor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class AncestorRuleEngine {

  private static final List<AncestorRule> rules = Arrays.asList(
    new AncestorWithTagRule(),
    new AncestorWithClassRule(),
    new AncestorWithAttributeRule(),
    new AncestorWithAttributeAndValueRule()
  );

  public AncestorResult process(String selector, int index) {
    return rules
      .stream()
      .map(rule -> rule.evaluate(selector, index))
      .flatMap(optional -> optional.map(Stream::of).orElseGet(Stream::empty))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Selector does not match any rule"));
  }
}
