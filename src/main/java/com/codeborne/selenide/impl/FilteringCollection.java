package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.impl.Alias.NONE;
import static java.util.stream.Collectors.toList;

@ParametersAreNonnullByDefault
public class FilteringCollection implements CollectionSource {
  private final CollectionSource originalCollection;
  private final Condition filter;
  private Alias alias = NONE;

  public FilteringCollection(CollectionSource originalCollection, Condition filter) {
    this.originalCollection = originalCollection;
    this.filter = filter;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public List<WebElement> getElements() {
    return originalCollection.getElements().stream()
      .filter(webElement -> filter.check(originalCollection.driver(), webElement).verdict() == ACCEPT)
      .collect(toList());
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebElement getElement(int index) {
    return originalCollection.getElements().stream()
      .filter(webElement -> filter.check(originalCollection.driver(), webElement).verdict() == ACCEPT)
      .skip(index)
      .findFirst()
      .orElseThrow(() -> new IndexOutOfBoundsException("Index: " + index));
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String getSearchCriteria() {
    return originalCollection.description() + ".filter(" + filter + ')';
  }

  @Override
  public String toString() {
    return originalCollection + ".filter(" + filter + ')';
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Driver driver() {
    return originalCollection.driver();
  }

  @Nonnull
  @Override
  public Alias getAlias() {
    return alias;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = new Alias(alias);
  }
}
