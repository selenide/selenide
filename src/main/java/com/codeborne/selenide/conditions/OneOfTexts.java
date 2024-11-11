package com.codeborne.selenide.conditions;

import com.codeborne.selenide.impl.Html;

import java.util.Collection;

import static com.codeborne.selenide.conditions.MultipleTextsCondition.BlankPolicy.BLANK_FORBIDDEN;

public class OneOfTexts extends MultipleTextsCondition {
  public OneOfTexts(Collection<String> targets) {
    super("one of texts", targets, BLANK_FORBIDDEN);
  }

  @Override
  protected boolean match(String actualText, String expectedText) {
    return targets.stream()
      .anyMatch(target -> Html.text.contains(actualText, target));
  }
}
