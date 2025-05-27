package com.codeborne.selenide.collections;

import com.codeborne.selenide.Config;

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
    return matches(config.textCheck(), actualText, expectedText);
  }

  @Override
  public String toString() {
    return "texts " + expectedTexts;
  }
}
