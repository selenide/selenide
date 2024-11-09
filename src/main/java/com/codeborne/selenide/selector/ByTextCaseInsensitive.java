package com.codeborne.selenide.selector;

import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class ByTextCaseInsensitive extends OptimizedBy {
  private static final JavaScript js = new JavaScript("find-elements-by-text-case-insensitive.js");
  private final String elementText;

  public ByTextCaseInsensitive(String elementText) {
    this.elementText = elementText;
  }

  @Override
  protected List<WebElement> findElements(SearchContext context, int limit) {
    return requireNonNull(js.execute(context, js.node(context), elementText, limit));
  }

  @Override
  public String toString() {
    return "by text (case-insensitive): " + elementText;
  }
}
