package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class BySelectorCollection implements WebElementsCollection {

  private final SearchContext parent;
  private final By selector;
  private List<WebElement> actualElements;

  public BySelectorCollection(By selector) {
    this(null, selector);
  }

  public BySelectorCollection(SearchContext parent, By selector) {
    this.parent = parent;
    this.selector = selector;
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
    SearchContext searchContext = parent == null ? getWebDriver() : parent;
    actualElements = WebElementSelector.instance.findElements(searchContext, selector);
    return actualElements;
  }

  @Override
  public String description() {
    return parent == null ? Describe.selector(selector) :
        (parent instanceof SelenideElement) ?
            ((SelenideElement) parent).getSearchCriteria() + "/" + Describe.shortly(selector) :
            Describe.shortly(selector);
  }
}
