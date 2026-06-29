package com.codeborne.selenide.selector;

import com.codeborne.selenide.TextMatchOptions;
import com.codeborne.selenide.impl.JavaScript;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class ByRole extends OptimizedBy {
  private static final JavaScript js = new JavaScript("find-elements-by-role.js");
  private final String role;
  @Nullable
  private final String accessibleName;
  private final TextMatchOptions options;

  public ByRole(String role) {
    this(role, null, TextMatchOptions.fullText());
  }

  public ByRole(String role, String accessibleName) {
    this(role, accessibleName, TextMatchOptions.fullText());
  }

  public ByRole(String role, @Nullable String accessibleName, TextMatchOptions options) {
    this.role = role;
    this.accessibleName = accessibleName;
    this.options = options;
  }

  @Override
  protected List<WebElement> findElements(SearchContext context, int limit) {
    return requireNonNull(js.execute(context, js.node(context), role, accessibleName, options.toMap(), limit));
  }

  @Override
  public String toString() {
    return accessibleName == null
      ? "by role \"%s\"".formatted(role)
      : "by role \"%s\" with name \"%s\" (%s)".formatted(role, accessibleName, options);
  }
}
