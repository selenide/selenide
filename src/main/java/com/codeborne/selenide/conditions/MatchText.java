package com.codeborne.selenide.conditions;

import com.codeborne.selenide.impl.Html;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MatchText extends TextCondition {

  public MatchText(String regex) {
    super("match text", regex);
  }

  @Override
  protected boolean match(String actualText, String expectedText) {
    return Html.text.matches(actualText, expectedText);
  }
}
