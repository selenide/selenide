package com.codeborne.selenide.selector;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Subclass of `By` optimized to quickly find ONE element.
 * The default Selenium implementation fetches ALL elements and returns the first of them.
 * This implementation fetches only the first element.
 * @since 5.22.0
 */
@ParametersAreNonnullByDefault
abstract class OptimizedBy extends By {
  @Nonnull
  @CheckReturnValue
  @Override
  public WebElement findElement(SearchContext context) {
    List<WebElement> allElements = findElements(context, 1);
    if (allElements.isEmpty()) {
      throw new NoSuchElementException("Cannot locate an element " + this);
    }
    return allElements.get(0);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public List<WebElement> findElements(SearchContext context) {
    return findElements(context, Integer.MAX_VALUE);
  }

  @Nonnull
  protected abstract List<WebElement> findElements(SearchContext context, int limit);
}
