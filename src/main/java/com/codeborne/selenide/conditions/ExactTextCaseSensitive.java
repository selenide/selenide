package com.codeborne.selenide.conditions;

import com.codeborne.selenide.impl.Html;

public class ExactTextCaseSensitive extends TextCondition {

  public ExactTextCaseSensitive(String expectedText) {
    super("exact text case sensitive", expectedText);
  }

  @Override
  protected boolean match(String actualText, String expectedText) {
    return Html.text.equalsCaseSensitive(actualText, expectedText);
  }
}
