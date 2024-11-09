package com.codeborne.selenide.conditions.webdriver;

public class CurrentFrameUrl extends CurrentFrameCondition {
  public CurrentFrameUrl(String expectedUrl) {
    super("", expectedUrl);
  }

  @Override
  public boolean test(String url) {
    return url.equals(expectedUrl);
  }
}
