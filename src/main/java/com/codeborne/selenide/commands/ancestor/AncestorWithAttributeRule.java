package com.codeborne.selenide.commands.ancestor;

import static java.lang.String.format;

public class AncestorWithAttributeRule extends SelectorValidation implements AncestorRule {

  private String xpath;

  @Override
  public boolean evaluate(String selector, int index) {
    boolean evaluationResult = false;
    if (isAttribute(selector) && !containsAttributeValue(selector)) {
      String attribute = selector.substring(1, selector.length() - 1);
      this.xpath = format("ancestor::*[@%s][%s]", attribute, index);
      evaluationResult = true;
    }
    return evaluationResult;
  }

  @Override
  public AncestorResult getAncestorResult() {
    return new AncestorResult(xpath);
  }
}
