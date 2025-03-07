package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.impl.Alias.NONE;
import static java.util.stream.Collectors.toList;

public class FilteringCollection implements CollectionSource {
  private final CollectionSource originalCollection;
  private final WebElementCondition filter;
  private Alias alias = NONE;

  public FilteringCollection(CollectionSource originalCollection, WebElementCondition filter) {
    this.originalCollection = originalCollection;
    this.filter = filter;
  }

  @Override
  public List<WebElement> getElements() {
    return originalCollection.getElements().stream()
      .filter(webElement -> filter.check(originalCollection.driver(), webElement).verdict() == ACCEPT)
      .collect(toList());
  }

  @Override
  public WebElement getElement(int index) {
    return originalCollection.getElements().stream()
      .filter(webElement -> filter.check(originalCollection.driver(), webElement).verdict() == ACCEPT)
      .skip(index)
      .findFirst()
      .orElseThrow(() -> new IndexOutOfBoundsException("Index: " + index));
  }

  @Override
  public String getSearchCriteria() {
    return originalCollection.description() + ".filter(" + filter + ')';
  }

  @Override
  public String toString() {
    return originalCollection + ".filter(" + filter + ')';
  }

  @Override
  public Driver driver() {
    return originalCollection.driver();
  }

  @Override
  public Alias getAlias() {
    return alias;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = new Alias(alias);
  }
}
