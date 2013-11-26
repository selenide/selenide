package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;

public class CollectionElement extends AbstractSelenideElement {
  public static SelenideElement wrap(WebElementsCollection collection, int index) {
    return (SelenideElement) Proxy.newProxyInstance(
            collection.getClass().getClassLoader(), new Class<?>[]{SelenideElement.class}, new CollectionElement(collection, index));
  }

  private final WebElementsCollection collection;
  private final int index;

  CollectionElement(WebElementsCollection collection, int index) {
    this.collection = collection;
    this.index = index;
  }


  @Override
  protected WebElement getDelegate() {
    return collection.getActualElements().get(index);
  }

  @Override
  protected WebElement getActualDelegate() throws NoSuchElementException, IndexOutOfBoundsException {
    return getDelegate();
  }

  @Override
  String getSearchCriteria() {
    return collection.description() + '[' + index  + ']';
  }

  @Override
  public String toString() {
    return getSearchCriteria();
  }
}