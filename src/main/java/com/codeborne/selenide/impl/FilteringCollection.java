package com.codeborne.selenide.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.openqa.selenium.WebElement;

import java.util.List;

public class FilteringCollection implements WebElementsCollection {
  private final WebElementsCollection originalCollection;
  private final Predicate<WebElement> filter;

  public FilteringCollection(WebElementsCollection originalCollection, Predicate<WebElement> filter) {
    this.originalCollection = originalCollection;
    this.filter = filter;
  }

  @Override
  public List<WebElement> getActualElements() {
    return Lists.newArrayList(Collections2.filter(originalCollection.getActualElements(), filter));
  }

  @Override
  public String description() {
    return originalCollection.description() + ".filter(" + filter + ')';
  }
}
