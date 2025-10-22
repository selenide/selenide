package com.codeborne.selenide.selector;

import com.codeborne.selenide.TextMatchOptions;
import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class SearchByAttribute extends OptimizedBy {
  private static final JavaScript js = new JavaScript("find-elements-by-attribute.js");
  private final String attributeName;
  private final String attributeValue;
  private final TextMatchOptions options;

  public SearchByAttribute(String attributeName, String attributeValue, TextMatchOptions options) {
    this.attributeName = attributeName;
    this.attributeValue = attributeValue;
    this.options = options;
  }

  @Override
  protected List<WebElement> findElements(SearchContext context, int limit) {
    return requireNonNull(js.execute(context, js.node(context), attributeName, attributeValue, options.toMap(), limit));
  }

  @Override
  public String toString() {
    return "by \"%s\"=\"%s\" (%s)".formatted(attributeName, attributeValue, options);
  }
}
