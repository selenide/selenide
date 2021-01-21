package com.codeborne.selenide.selector;

import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ByTextCaseInsensitive extends By {
  private static final JavaScript js = new JavaScript("find-elements-by-text-case-insensitive.js");
  private final String elementText;

  public ByTextCaseInsensitive(String elementText) {
    this.elementText = elementText;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public WebElement findElement(SearchContext context) {
    // TODO find only the first element effectively
    return super.findElement(context);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public List<WebElement> findElements(SearchContext context) {
    return js.execute(context, js.node(context), elementText);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return "by text (case-insensitive): " + elementText;
  }
}
