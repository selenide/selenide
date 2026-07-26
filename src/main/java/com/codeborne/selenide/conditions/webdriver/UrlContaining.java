package com.codeborne.selenide.conditions.webdriver;

import org.jspecify.annotations.Nullable;

public class UrlContaining extends UrlCondition {
  public UrlContaining(String expectedUrl) {
    super("url containing", expectedUrl);
  }

  @Override
  public String description() {
    return "url containing \"%s\"".formatted(expectedUrl);
  }

  @Override
  public String negativeDescription() {
    return description();
  }

  @Override
  public boolean test(@Nullable String url) {
    return url != null && url.contains(expectedUrl);
  }
}
