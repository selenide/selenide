package com.codeborne.selenide.commands.closest;

import static java.lang.String.format;

public class ClosestWithClassRule extends SelectorValidation implements ClosestRule {

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
  public ClosestResult getClosestResult() {
    return new ClosestResult(xpath);
  }
}
