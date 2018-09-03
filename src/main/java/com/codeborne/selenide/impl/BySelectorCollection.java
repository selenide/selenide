package com.codeborne.selenide.impl;

import com.codeborne.selenide.Context;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

public class BySelectorCollection implements WebElementsCollection {

  private final Context context;
  private final SearchContext parent;
  private final By selector;

  public BySelectorCollection(Context context, By selector) {
    this(context, null, selector);
  }

  public BySelectorCollection(Context context, SearchContext parent, By selector) {
    this.context = context;
    this.parent = parent;
    this.selector = selector;
  }

  @Override
  public List<WebElement> getElements() {
    SearchContext searchContext = parent == null ? context.getWebDriver() : parent;
    return WebElementSelector.instance.findElements(context, searchContext, selector);
  }

  @Override
  public String description() {
    return parent == null ? Describe.selector(selector) :
        (parent instanceof SelenideElement) ?
            ((SelenideElement) parent).getSearchCriteria() + "/" + Describe.shortly(selector) :
            Describe.shortly(selector);
  }

  @Override
  public Context context() {
    return context;
  }
}
