package com.codeborne.selenide.conditions;

import com.codeborne.selenide.impl.Html;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class OneOfExactTextsCaseSensitive extends TextCondition {
  private final Collection<String> targets;

  public OneOfExactTextsCaseSensitive(Collection<String> targets) {
    super("one of exact texts case sensitive", targets.toString());
    if (targets.isEmpty()) {
      throw new IllegalArgumentException("No expected texts given");
    }
    if (targets.stream().anyMatch(Objects::isNull)) {
      throw new IllegalArgumentException("The collection must not contain null");
    }
    this.targets = targets;
  }

  @CheckReturnValue
  @Override
  protected boolean match(String actualText, String expectedText) {
    return targets.stream()
      .anyMatch(target -> Html.text.equalsCaseSensitive(actualText, target));
  }
}
