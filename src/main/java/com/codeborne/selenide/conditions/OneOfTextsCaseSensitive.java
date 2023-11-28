package com.codeborne.selenide.conditions;

import com.codeborne.selenide.impl.Html;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

import static com.codeborne.selenide.conditions.MultipleTextsCondition.BlankPolicy.BLANK_FORBIDDEN;

@ParametersAreNonnullByDefault
public class OneOfTextsCaseSensitive extends MultipleTextsCondition {
  public OneOfTextsCaseSensitive(Collection<String> targets) {
    super("one of texts case sensitive", targets, BLANK_FORBIDDEN);
  }

  @CheckReturnValue
  @Override
  protected boolean match(String actualText, String expectedText) {
    return targets.stream()
      .anyMatch(target -> Html.text.containsCaseSensitive(actualText, target));
  }
}
