package com.codeborne.selenide.impl;

import org.openqa.selenium.WebElement;

import java.util.List;

public class HeadOfCollection implements WebElementsCollection {
  private final WebElementsCollection originalCollection;
  private final int size;
  private List<WebElement> actualElements;

  public HeadOfCollection(WebElementsCollection originalCollection, int size) {
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
    this.actualElements = source.subList(0, Math.min(source.size(), size));
    return this.actualElements;
  }

  @Override
  public String description() {
    return originalCollection.description() + ".first(" + size + ')';
  }
}
