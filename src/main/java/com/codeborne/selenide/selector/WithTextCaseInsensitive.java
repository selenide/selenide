package com.codeborne.selenide.selector;

import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class WithTextCaseInsensitive extends OptimizedBy {
  private static final JavaScript js = new JavaScript("find-elements-with-text-case-insensitive.js");
  private final String elementText;

  public WithTextCaseInsensitive(String elementText) {
    this.elementText = elementText;
  }

  @Override
  @Nonnull
  protected List<WebElement> findElements(SearchContext context, int limit) {
    return js.execute(context, js.node(context), elementText, limit);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return "with text (case-insensitive): " + elementText;
  }
}
