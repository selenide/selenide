package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.util.stream.Collectors.toList;

@ParametersAreNonnullByDefault
public class FilteringCollection implements WebElementsCollection {
  private final WebElementsCollection originalCollection;
  private final Condition filter;

  public FilteringCollection(WebElementsCollection originalCollection, Condition filter) {
    this.originalCollection = originalCollection;
    this.filter = filter;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public List<WebElement> getElements() {
    return originalCollection.getElements().stream()
      .filter(webElement -> filter.apply(originalCollection.driver(), webElement))
      .collect(toList());
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebElement getElement(int index) {
    return originalCollection.getElements().stream()
      .filter(webElement -> filter.apply(originalCollection.driver(), webElement))
      .skip(index)
      .findFirst()
      .orElseThrow(() -> new IndexOutOfBoundsException("Index: " + index));
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String description() {
    return originalCollection.description() + ".filter(" + filter + ')';
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Driver driver() {
    return originalCollection.driver();
  }
}
