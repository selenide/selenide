package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Context;
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
    this.filter = new ConditionPredicate(context(), filter);
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
  public Context context() {
    return originalCollection.context();
  }

  private static class ConditionPredicate implements Predicate<WebElement> {
    private final Context context;
    private final Condition filter;

    private ConditionPredicate(Context context, Condition filter) {
      this.context = context;
      this.filter = filter;
    }

    @Override
    public boolean apply(@NullableDecl WebElement webElement) {
      return filter.apply(context, webElement);
    }

    @Override
    public String toString() {
      return filter.toString();
    }
  }
}
