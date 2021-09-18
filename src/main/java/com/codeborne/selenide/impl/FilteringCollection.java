package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

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
    return alias.getOrElse(() -> originalCollection.description() + ".filter(" + filter + ')');
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Driver driver() {
    return originalCollection.driver();
  }

  @Override
  public void setAlias(String alias) {
    this.alias = new Alias(alias);
  }
}
