package com.codeborne.selenide.commands.ancestor;

import java.util.Optional;

import static java.lang.String.format;

public class AncestorWithClassRule extends SelectorValidation implements AncestorRule {

  @Override
  public Optional<AncestorResult> evaluate(String selector, int index) {
    if (isCssClass(selector)) {
      String xpath = format(
        "ancestor::*[contains(concat(' ', normalize-space(@class), ' '), ' %s ')][%s]",
        selector.substring(1),
        index
      );
      return Optional.of(new AncestorResult(xpath));
    }
    return Optional.empty();
  }
}
