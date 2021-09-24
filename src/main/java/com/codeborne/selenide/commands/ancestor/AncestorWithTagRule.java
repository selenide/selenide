package com.codeborne.selenide.commands.ancestor;

import static java.lang.String.format;

public class AncestorWithTagRule extends SelectorValidation implements AncestorRule {

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
  public AncestorResult getAncestorResult() {
    return new AncestorResult(xpath);
  }
}
