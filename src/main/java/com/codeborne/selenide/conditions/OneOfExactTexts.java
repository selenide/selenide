package com.codeborne.selenide.conditions;

import com.codeborne.selenide.impl.Html;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

import static com.codeborne.selenide.conditions.MultipleTextsCondition.BlankPolicy.BLANK_ALLOWED;

@ParametersAreNonnullByDefault
public class OneOfExactTexts extends MultipleTextsCondition {
  public OneOfExactTexts(Collection<String> targets) {
    super("one of exact texts", targets, BLANK_ALLOWED);
  }

  @CheckReturnValue
  @Override
  protected boolean match(String actualText, String expectedText) {
    return targets.stream()
      .anyMatch(target -> Html.text.equals(actualText, target));
  }
}
