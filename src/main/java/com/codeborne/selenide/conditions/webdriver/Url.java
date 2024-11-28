package com.codeborne.selenide.conditions.webdriver;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class Url extends UrlCondition {
  public Url(String expectedUrl) {
    super("", expectedUrl);
  }

  @Override
  public boolean test(@Nullable String url) {
    return Objects.equals(url, expectedUrl);
  }
}
