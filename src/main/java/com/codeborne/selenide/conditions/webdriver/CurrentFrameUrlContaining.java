package com.codeborne.selenide.conditions.webdriver;

public class CurrentFrameUrlContaining extends CurrentFrameCondition {
  public CurrentFrameUrlContaining(String expectedUrl) {
    super("containing ", expectedUrl);
  }

  @Override
  public boolean test(String url) {
    return url.contains(expectedUrl);
  }
}
