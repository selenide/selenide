package com.codeborne.selenide.impl;

import com.google.common.base.Predicate;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Collections2.filter;

public class FilteringCollection implements WebElementsCollection {
  private final WebElementsCollection originalCollection;
  private final Predicate<WebElement> filter;
  private List<WebElement> actualElements;

  public FilteringCollection(WebElementsCollection originalCollection, Predicate<WebElement> filter) {
    this.originalCollection = originalCollection;
    this.filter = filter;
  }

  @Override
  public List<WebElement> getElements() {
    if (actualElements == null) {
      return getActualElements();
    }
    return actualElements;
  }

  @Override
  public List<WebElement> getActualElements() {
    actualElements = new ArrayList<>(filter(originalCollection.getActualElements(), filter));
    return actualElements;
  }

  @Override
  public String description() {
    return originalCollection.description() + ".filter(" + filter + ')';
  }
}
