package com.codeborne.selenide.commands.ancestor;

import java.util.Optional;

import static java.lang.String.format;

public class AncestorWithAttributeRule extends SelectorValidation implements AncestorRule {

  @Override
  public Optional<AncestorResult> evaluate(String selector, int index) {
    if (isAttribute(selector) && !containsAttributeValue(selector)) {
      String attribute = selector.substring(1, selector.length() - 1);
      String xpath = format("ancestor::*[@%s][%s]", attribute, index);
      return Optional.of(new AncestorResult(xpath));
    }
    return Optional.empty();
  }
}
