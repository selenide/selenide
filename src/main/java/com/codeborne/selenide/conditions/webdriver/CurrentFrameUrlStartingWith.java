package com.codeborne.selenide.conditions.webdriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CurrentFrameUrlStartingWith extends CurrentFrameCondition {
  public CurrentFrameUrlStartingWith(String expectedUrl) {
    super("starting with ", expectedUrl);
  }

  @CheckReturnValue
  @Override
  public boolean test(String url) {
    return url.startsWith(expectedUrl);
  }
}
