package com.codeborne.selenide.commands.ancestor;

import java.util.Optional;

import static java.lang.String.format;

public class AncestorWithAttributeAndValueRule extends SelectorValidation implements AncestorRule {

  @Override
  public Optional<AncestorResult> evaluate(String selector, int index) {
    if (isAttribute(selector) && containsAttributeValue(selector)) {
      int endIndex = selector.indexOf("=");
      String attribute = selector.substring(1, endIndex);
      String attributeValue = selector.substring(endIndex + 1, selector.length() - 1);
      String xpath = format("ancestor::*[@%s='%s'][%s]", attribute, attributeValue, index);
      return Optional.of(new AncestorResult(xpath));
    }
    return Optional.empty();
  }
}
