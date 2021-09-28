package com.codeborne.selenide.conditions;

import com.codeborne.selenide.impl.Html;

import javax.annotation.ParametersAreNonnullByDefault;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@ParametersAreNonnullByDefault
public class MatchText extends TextCondition {

  public MatchText(String regex) {
    super("match text", regex);
    if (isEmpty(regex)) {
      throw new IllegalArgumentException("Argument must not be null or empty string");
    }
  }

  @Override
  protected boolean match(String actualText, String expectedText) {
    return Html.text.matches(actualText, expectedText);
  }
}
