package com.codeborne.selenide.conditions;

import com.codeborne.selenide.TextCheck;
import com.codeborne.selenide.impl.Html;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
abstract class CaseInsensitiveTextCondition extends TextCondition {
  protected CaseInsensitiveTextCondition(String name, String expectedText) {
    super(name, expectedText);
  }

  @CheckReturnValue
  @Override
  protected boolean match(String actualText, String expectedText) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected boolean match(TextCheck textCheck, String actualText, String expectedText) {
    return switch (textCheck) {
      case FULL_TEXT -> Html.text.equals(actualText, expectedText);
      case PARTIAL_TEXT -> Html.text.contains(actualText, expectedText);
    };
  }
}
