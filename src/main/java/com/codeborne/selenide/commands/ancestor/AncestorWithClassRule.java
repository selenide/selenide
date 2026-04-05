package com.codeborne.selenide.commands.ancestor;

import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public class AncestorWithClassRule extends SelectorValidation implements AncestorRule {

  @Override
  public Optional<AncestorResult> evaluate(String selector, int index) {
    if (isCssClass(selector)) {
      String[] classNames = selector.split("\\.");
      String classesSelector = Stream.of(classNames)
        .filter(className -> !className.isEmpty())
        .map(className -> "contains(concat(' ', normalize-space(@class), ' '), ' %s ')".formatted(className))
        .collect(joining(" and "));
      String xpath = format(
        "ancestor::*[ %s ][%s]",
        classesSelector,
        index
      );
      return Optional.of(new AncestorResult(xpath));
    }
    return Optional.empty();
  }
}
