package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class HeadOfCollection implements WebElementsCollection {
  private final WebElementsCollection originalCollection;
  private final int size;

  public HeadOfCollection(WebElementsCollection originalCollection, int size) {
    this.originalCollection = originalCollection;
    this.size = size;
  }

  @Override
  public Driver driver() {
    return originalCollection.driver();
  }

  @Override
  public List<WebElement> getElements() {
    List<WebElement> source = originalCollection.getElements();
    return source.subList(0, Math.min(source.size(), size));
  }

  @Override
  public String description() {
    return originalCollection.description() + ".first(" + size + ')';
  }
}
