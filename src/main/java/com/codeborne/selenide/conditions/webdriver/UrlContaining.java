package com.codeborne.selenide.conditions.webdriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class UrlContaining extends UrlCondition {
  public UrlContaining(String expectedUrl) {
    super("containing ", expectedUrl);
  }

  @CheckReturnValue
  @Override
  public boolean test(String url) {
    return url.contains(expectedUrl);
  }
}
