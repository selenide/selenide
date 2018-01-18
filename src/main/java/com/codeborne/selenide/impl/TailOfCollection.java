package com.codeborne.selenide.impl;

import org.openqa.selenium.WebElement;

import java.util.List;

public class TailOfCollection implements WebElementsCollection {
  private final WebElementsCollection originalCollection;
  private final int size;
  private List<WebElement> actualElements;

  public TailOfCollection(WebElementsCollection originalCollection, int size) {
    this.originalCollection = originalCollection;
    this.size = size;
  }

  @Override
  public List<WebElement> getElements() {
    if (actualElements == null) {
      return getActualElements();
    }
    return actualElements;
  }

  @Override
  public List<WebElement> getActualElements() {
    List<WebElement> source = originalCollection.getActualElements();
    this.actualElements = source.subList(source.size() - Math.min(source.size(), size), source.size());
    return this.actualElements;
  }

  @Override
  public String description() {
    return originalCollection.description() + ".last(" + size + ')';
  }
}
