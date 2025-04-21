package com.codeborne.selenide.collections;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.impl.Html;

import java.util.List;

public class Texts extends ExactTexts {
  public Texts(String... expectedTexts) {
    super(expectedTexts);
  }

  public Texts(List<String> expectedTexts) {
    super(expectedTexts);
  }

  @Override
  protected boolean check(Config config, String actualText, String expectedText) {
    return switch (config.textCheck()) {
      case PARTIAL_TEXT -> Html.text.contains(actualText, expectedText);
      case FULL_TEXT -> Html.text.equals(actualText, expectedText);
    };
  }

  @Override
  public String toString() {
    return "texts " + expectedTexts;
  }
}
