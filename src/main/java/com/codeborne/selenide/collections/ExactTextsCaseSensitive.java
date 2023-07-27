package com.codeborne.selenide.collections;

import com.codeborne.selenide.impl.Html;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ExactTextsCaseSensitive extends ExactTexts {
  public ExactTextsCaseSensitive(String... expectedTexts) {
    super(expectedTexts);
  }

  public ExactTextsCaseSensitive(List<String> expectedTexts) {
    super(expectedTexts);
  }

  @CheckReturnValue
  @Override
  protected boolean check(String actualText, String expectedText) {
    return Html.text.equalsCaseSensitive(actualText, expectedText);
  }

  @Override
  public String toString() {
    return "Exact texts case sensitive " + expectedTexts;
  }
}
