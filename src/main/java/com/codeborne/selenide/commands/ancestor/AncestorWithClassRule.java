package com.codeborne.selenide.commands.ancestor;

import static java.lang.String.format;

public class AncestorWithClassRule extends SelectorValidation implements AncestorRule {

  private String xpath;

  @Override
  public boolean evaluate(String selector, int index) {
    boolean evaluationResult = false;
    if (isCssClass(selector)) {
      this.xpath =
        format(
          "ancestor::*[contains(concat(' ', normalize-space(@class), ' '), ' %s ')][%s]",
          selector.substring(1),
          index
        );
      evaluationResult = true;
    }
    return evaluationResult;
  }

  @Override
  public AncestorResult getAncestorResult() {
    return new AncestorResult(xpath);
  }
}
