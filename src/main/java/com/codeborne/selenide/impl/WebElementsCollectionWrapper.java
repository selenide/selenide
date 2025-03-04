package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.codeborne.selenide.impl.Alias.NONE;

public class WebElementsCollectionWrapper implements CollectionSource {
  private final List<WebElement> elements;
  private final Driver driver;
  private Alias alias = NONE;

  public WebElementsCollectionWrapper(Driver driver, Collection<? extends WebElement> elements) {
    this.driver = driver;
    this.elements = new ArrayList<>(elements);
  }

  @Override
  public List<WebElement> getElements() {
    return elements;
  }

  @Override
  public WebElement getElement(int index) {
    return elements.get(index);
  }

  @Override
  public String getSearchCriteria() {
    return "$$(" + elements.size() + " elements)";
  }

  @Override
  public String toString() {
    return getSearchCriteria();
  }

  @Override
  public String description() {
    return alias.getOrElse(() -> getSearchCriteria());
  }

  @Override
  public Driver driver() {
    return driver;
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
