package com.codeborne.selenide.commands.closest;

import static java.lang.String.format;

public class ClosestWithAttributeAndValueRule extends SelectorValidation implements ClosestRule {

  private String xpath;

  @Override
  public boolean evaluate(String selector) {
    boolean evaluationResult = false;
    if (isAttribute(selector) && containsAttributeValue(selector)) {
      String attribute = selector.substring(1, selector.indexOf("="));
      String attributeValue = selector.substring(selector.indexOf("=") + 1, selector.length() - 1);
      this.xpath = format("ancestor::*[@%s='%s'][1]", attribute, attributeValue);
      evaluationResult = true;
    }
    return evaluationResult;
  }

  @Override
  public ClosestResult getClosestResult() {
    return new ClosestResult(xpath);
  }
}
