package com.codeborne.selenide.conditions.webdriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CurrentFrameUrl extends CurrentFrameCondition {
  public CurrentFrameUrl(String expectedUrl) {
    super("", expectedUrl);
  }

  @CheckReturnValue
  @Override
  public boolean test(String url) {
    return url.equals(expectedUrl);
  }
}
