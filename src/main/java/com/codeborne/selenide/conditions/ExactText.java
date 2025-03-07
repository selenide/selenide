package com.codeborne.selenide.conditions;

import com.codeborne.selenide.impl.Html;

public class ExactText extends TextCondition {

  public ExactText(String expectedText) {
    super("exact text", expectedText);
  }

  @Override
  protected boolean match(String actualText, String expectedText) {
    return Html.text.equals(actualText, expectedText);
  }
}
