package com.codeborne.selenide.commands.closest;

import static java.lang.String.format;

public class ClosestWithTagRule extends SelectorValidation implements ClosestRule {

  private String xpath;

  @Override
  public boolean evaluate(String selector, int index) {
    boolean evaluationResult = false;
    if (!isCssClass(selector) && !isAttribute(selector) && !containsAttributeValue(selector)) {
      this.xpath = format("ancestor::%s[%s]", selector, index);
      evaluationResult = true;
    }
    return evaluationResult;
  }

  @Override
  public ClosestResult getClosestResult() {
    return new ClosestResult(xpath);
  }
}
