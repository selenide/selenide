package com.codeborne.selenide.selector;

import com.codeborne.selenide.TextMatchOptions;
import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class ByLabel extends OptimizedBy {
  private static final JavaScript js = new JavaScript("find-elements-by-label.js");
  private final String elementText;
  private final TextMatchOptions options;

  public ByLabel(String elementText, TextMatchOptions options) {
    this.elementText = elementText;
    this.options = options;
  }

  @Override
  protected List<WebElement> findElements(SearchContext context, int limit) {
    return requireNonNull(js.execute(context, js.node(context), elementText, options.toMap(), limit));
  }

  @Override
  public String toString() {
    return "by label \"%s\" (%s)".formatted(elementText, options);
  }
}
