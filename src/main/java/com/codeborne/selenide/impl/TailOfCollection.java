package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class TailOfCollection implements WebElementsCollection {
  private final WebElementsCollection originalCollection;
  private final int size;

  public TailOfCollection(WebElementsCollection originalCollection, int size) {
    this.originalCollection = originalCollection;
    this.size = size;
  }

  @Override
  public List<WebElement> getElements() {
    List<WebElement> source = originalCollection.getElements();
    return source.subList(source.size() - Math.min(source.size(), size), source.size());
  }

  @Override
  public String description() {
    return originalCollection.description() + ".last(" + size + ')';
  }

  @Override
  public Driver driver() {
    return originalCollection.driver();
  }
}
