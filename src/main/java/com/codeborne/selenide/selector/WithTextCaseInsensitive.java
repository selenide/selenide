package com.codeborne.selenide.selector;

import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class WithTextCaseInsensitive extends OptimizedBy {
  private static final JavaScript js = new JavaScript("find-elements-with-text-case-insensitive.js");
  private final String elementText;

  public WithTextCaseInsensitive(String elementText) {
    this.elementText = elementText;
  }

  @Override
  protected List<WebElement> findElements(SearchContext context, int limit) {
    return requireNonNull(js.execute(context, js.node(context), elementText, limit));
  }

  @Override
  public String toString() {
    return "with text (case-insensitive): " + elementText;
  }
}
