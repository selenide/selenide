package com.codeborne.selenide.collections;

import com.codeborne.selenide.impl.Html;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class Texts extends ExactTexts {
  public Texts(String... expectedTexts) {
    super(expectedTexts);
  }

  public Texts(List<String> expectedTexts) {
    super(expectedTexts);
  }

  @Override
  protected boolean check(String actualText, String expectedText) {
    return Html.text.contains(actualText, expectedText);
  }

  @Override
  public String toString() {
    return "texts " + expectedTexts;
  }
}
