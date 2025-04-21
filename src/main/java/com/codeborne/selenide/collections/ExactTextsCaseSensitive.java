package com.codeborne.selenide.collections;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.impl.Html;

import java.util.List;

public class ExactTextsCaseSensitive extends ExactTexts {
  public ExactTextsCaseSensitive(String... expectedTexts) {
    super(expectedTexts);
  }

  public ExactTextsCaseSensitive(List<String> expectedTexts) {
    super(expectedTexts);
  }

  @Override
  protected boolean check(Config config, String actualText, String expectedText) {
    return Html.text.equalsCaseSensitive(actualText, expectedText);
  }

  @Override
  public String toString() {
    return "Exact texts case sensitive " + expectedTexts;
  }
}
