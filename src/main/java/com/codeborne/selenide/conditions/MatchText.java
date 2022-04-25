package com.codeborne.selenide.conditions;

import com.codeborne.selenide.TextCheck;
import com.codeborne.selenide.impl.Html;

import javax.annotation.CheckReturnValue;
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

  @CheckReturnValue
  @Override
  protected boolean match(String actualText, String expectedText) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected boolean match(TextCheck textCheck, String actualText, String expectedText) {
    return switch (textCheck) {
      case FULL_TEXT -> Html.text.matches(actualText, expectedText);
      case PARTIAL_TEXT -> Html.text.matchesSubstring(actualText, expectedText);
    };
  }
}
