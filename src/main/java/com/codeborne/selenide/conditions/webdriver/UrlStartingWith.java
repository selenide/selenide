package com.codeborne.selenide.conditions.webdriver;

import org.jspecify.annotations.Nullable;

public class UrlStartingWith extends UrlCondition {
  public UrlStartingWith(String expectedUrl) {
    super("starting with ", expectedUrl);
  }

  @Override
  public boolean test(@Nullable String url) {
    return url != null && url.startsWith(expectedUrl);
  }
}
