package com.codeborne.selenide.commands.ancestor;

public class SelectorValidation {

  protected boolean isCssClass(String selector) {
    return selector.startsWith(".");
  }

  protected boolean isAttribute(String selector) {
    return selector.startsWith("[") && selector.endsWith("]");
  }

  protected boolean containsAttributeValue(String selector) {
    return selector.contains("=");
  }
}
