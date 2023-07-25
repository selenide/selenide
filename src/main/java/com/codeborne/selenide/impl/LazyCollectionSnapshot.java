package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LazyCollectionSnapshot implements CollectionSource {

  private final CollectionSource delegate;

  private List<WebElement> elementsSnapshot;

  LazyCollectionSnapshot(CollectionSource delegate) {
    this.delegate = delegate;
  }

  @Nonnull
  @Override
  public List<WebElement> getElements() {
    if (elementsSnapshot == null) {
      elementsSnapshot = new ArrayList<>(delegate.getElements());
    }
    return elementsSnapshot;
  }

  @Nonnull
  @Override
  public WebElement getElement(int index) {
    return this.getElements().get(index);
  }

  @Nonnull
  @Override
  public String getSearchCriteria() {
    return delegate.getSearchCriteria();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Nonnull
  @Override
  public Driver driver() {
    return delegate.driver();
  }

  @Nonnull
  @Override
  public Alias getAlias() {
    return delegate.getAlias();
  }

  @Override
  public void setAlias(String alias) {
    delegate.setAlias(alias);
  }
}
