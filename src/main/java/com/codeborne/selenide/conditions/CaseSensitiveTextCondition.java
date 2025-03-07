package com.codeborne.selenide.conditions;

import com.codeborne.selenide.TextCheck;
import com.codeborne.selenide.impl.Html;

abstract class CaseSensitiveTextCondition extends TextCondition {
  protected CaseSensitiveTextCondition(String name, String expectedText) {
    super(name, expectedText);
  }

  @Override
  protected boolean match(String actualText, String expectedText) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected boolean match(TextCheck textCheck, String actualText, String expectedText) {
    return switch (textCheck) {
      case FULL_TEXT -> Html.text.equalsCaseSensitive(actualText, expectedText);
      case PARTIAL_TEXT -> Html.text.containsCaseSensitive(actualText, expectedText);
    };
  }
}
