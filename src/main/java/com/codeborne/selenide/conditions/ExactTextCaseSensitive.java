package com.codeborne.selenide.conditions;

import com.codeborne.selenide.impl.Html;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExactTextCaseSensitive extends TextCondition {

  public ExactTextCaseSensitive(String expectedText) {
    super("exact text case sensitive", expectedText);
  }

  @CheckReturnValue
  @Override
  protected boolean match(String actualText, String expectedText) {
    return Html.text.equalsCaseSensitive(actualText, expectedText);
  }
}
