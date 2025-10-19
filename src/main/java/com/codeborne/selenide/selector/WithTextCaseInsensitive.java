package com.codeborne.selenide.selector;

import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.TextMatchOptions.partialText;
import static java.util.Objects.requireNonNull;

public class WithTextCaseInsensitive extends OptimizedBy {
  private static final JavaScript js = new JavaScript("find-elements-by-text.js");
  private final String elementText;

  public WithTextCaseInsensitive(String elementText) {
    this.elementText = elementText;
  }

  @Override
  protected List<WebElement> findElements(SearchContext context, int limit) {
    return requireNonNull(js.execute(context, js.node(context), elementText, partialText().caseInsensitive().toMap(), limit));
  }

  @Override
  public String toString() {
    return "with text (case-insensitive): " + elementText;
  }
}
