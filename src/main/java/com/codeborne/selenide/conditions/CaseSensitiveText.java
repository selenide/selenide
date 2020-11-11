package com.codeborne.selenide.conditions;

import com.codeborne.selenide.impl.Html;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CaseSensitiveText extends TextCondition {

  public CaseSensitiveText(String expectedText) {
    super("textCaseSensitive", expectedText);
  }

  @Override
  protected boolean match(String actualText, String expectedText) {
    return Html.text.containsCaseSensitive(actualText, expectedText);
  }
}
