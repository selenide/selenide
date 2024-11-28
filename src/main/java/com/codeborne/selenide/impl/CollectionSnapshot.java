package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.impl.Alias.NONE;

public class CollectionSnapshot implements CollectionSource {

  private final CollectionSource originalCollection;
  private final List<WebElement> elementsSnapshot;
  private Alias alias = NONE;

  public CollectionSnapshot(CollectionSource collection) {
    this.originalCollection = collection;
    this.elementsSnapshot = new ArrayList<>(collection.getElements());
  }

  @Override
  public List<WebElement> getElements() {
    return elementsSnapshot;
  }

  @Override
  public WebElement getElement(int index) {
    return elementsSnapshot.get(index);
  }

  @Override
  public String getSearchCriteria() {
    return String.format("%s.snapshot(%d elements)", originalCollection.description(), elementsSnapshot.size());
  }

  @Override
  public String toString() {
    return getSearchCriteria();
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
