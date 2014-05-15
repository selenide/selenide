package com.codeborne.selenide.impl;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class BySelectorCollection implements WebElementsCollection {

  private final WebElement parent;
  private final By selector;

  public BySelectorCollection(By selector) {
    this(null, selector);
  }

  public BySelectorCollection(WebElement parent, By selector) {
    this.parent = parent;
    this.selector = selector;
  }

  @Override
  public List<WebElement> getActualElements() {
    SearchContext searchContext = parent == null ? getWebDriver() : parent;
    return searchContext.findElements(selector);
  }

  @Override
  public String description() {
    return parent == null ? selector.toString() : Describe.shortly(parent) + "/" + Describe.shortly(selector);
  }
}
