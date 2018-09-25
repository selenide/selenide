package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.google.common.base.Predicate;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Collections2.filter;

public class FilteringCollection implements WebElementsCollection {
  private final WebElementsCollection originalCollection;
  private final Predicate<WebElement> filter;

  public FilteringCollection(WebElementsCollection originalCollection, Predicate<WebElement> filter) {
    this.originalCollection = originalCollection;
    this.filter = filter;
  }

  public FilteringCollection(WebElementsCollection originalCollection, Condition filter) {
    this.originalCollection = originalCollection;
    this.filter = new ConditionPredicate(driver(), filter);
  }

  @Override
  public List<WebElement> getElements() {
    return new ArrayList<>(filter(originalCollection.getElements(), filter));
  }

  @Override
  public String description() {
    return originalCollection.description() + ".filter(" + filter + ')';
  }

  @Override
  public Driver driver() {
    return originalCollection.driver();
  }

  private static class ConditionPredicate implements Predicate<WebElement> {
    private final Driver driver;
    private final Condition filter;

    private ConditionPredicate(Driver driver, Condition filter) {
      this.driver = driver;
      this.filter = filter;
    }

    @Override
    public boolean apply(@NullableDecl WebElement webElement) {
      return filter.apply(driver, webElement);
    }

    @Override
    public String toString() {
      return filter.toString();
    }
  }
}
