package com.codeborne.selenide.impl;

import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WebElementsCollectionWrapper implements WebElementsCollection {
  private final List<WebElement> elements;

  public WebElementsCollectionWrapper(Collection<? extends WebElement> elements) {
    this.elements = new ArrayList<WebElement>(elements.size());
    this.elements.addAll(elements);
  }

  @Override
  public List<WebElement> getActualElements() {
    return elements;
  }

  @Override
  public String description() {
    return "$$(" + elements.size() + " elements)";
  }
}
