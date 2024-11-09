package com.codeborne.selenide.conditions.webdriver;

import org.jspecify.annotations.Nullable;

public class UrlContaining extends UrlCondition {
  public UrlContaining(String expectedUrl) {
    super("containing ", expectedUrl);
  }

  @Override
  public boolean test(@Nullable String url) {
    return url != null && url.contains(expectedUrl);
  }
}
