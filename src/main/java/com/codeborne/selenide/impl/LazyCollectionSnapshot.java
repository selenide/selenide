package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.impl.Lazy.lazyEvaluated;

public class LazyCollectionSnapshot implements CollectionSource {

  private final CollectionSource delegate;
  private final Lazy<List<WebElement>> elementsSnapshot;

  LazyCollectionSnapshot(CollectionSource delegate) {
    this.delegate = delegate;
    this.elementsSnapshot = lazyEvaluated(() -> new ArrayList<>(delegate.getElements()));
  }

  @Override
  public List<WebElement> getElements() {
    return elementsSnapshot.get();
  }

  @Override
  public WebElement getElement(int index) {
    return this.getElements().get(index);
  }

  @Override
  public String getSearchCriteria() {
    return delegate.getSearchCriteria();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public Driver driver() {
    return delegate.driver();
  }

  @Override
  public Alias getAlias() {
    return delegate.getAlias();
  }

  @Override
  public void setAlias(String alias) {
    delegate.setAlias(alias);
  }
}
