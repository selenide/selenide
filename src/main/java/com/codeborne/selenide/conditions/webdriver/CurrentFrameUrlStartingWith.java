package com.codeborne.selenide.conditions.webdriver;

public class CurrentFrameUrlStartingWith extends CurrentFrameCondition {
  public CurrentFrameUrlStartingWith(String expectedUrl) {
    super("starting with ", expectedUrl);
  }

  @Override
  public boolean test(String url) {
    return url.startsWith(expectedUrl);
  }
}
