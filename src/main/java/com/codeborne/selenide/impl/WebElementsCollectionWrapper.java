package com.codeborne.selenide.impl;

import com.codeborne.selenide.Context;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WebElementsCollectionWrapper implements WebElementsCollection {
  private final List<WebElement> elements;
  private final Context context;

  public WebElementsCollectionWrapper(Context context, Collection<? extends WebElement> elements) {
    this.context = context;
    this.elements = new ArrayList<>(elements);
  }

  @Override
  public List<WebElement> getElements() {
    return elements;
  }

  @Override
  public String description() {
    return "$$(" + elements.size() + " elements)";
  }

  @Override
  public Context context() {
    return context;
  }
}
