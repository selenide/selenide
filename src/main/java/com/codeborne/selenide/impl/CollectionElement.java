package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;

import static com.codeborne.selenide.Condition.visible;

public class CollectionElement extends WebElementSource {
  public static SelenideElement wrap(WebElementsCollection collection, int index) {
    return (SelenideElement) Proxy.newProxyInstance(
        collection.getClass().getClassLoader(), new Class<?>[]{SelenideElement.class},
        new SelenideElementProxy(new CollectionElement(collection, index)));
  }

  private final WebElementsCollection collection;
  private final int index;

  CollectionElement(WebElementsCollection collection, int index) {
    this.collection = collection;
    this.index = index;
  }

  @Override
  public Driver driver() {
    return collection.driver();
  }

  @Override
  public WebElement getWebElement() {
    return collection.getElements().get(index);
  }

  @Override
  public String getSearchCriteria() {
    return collection.description() + '[' + index  + ']';
  }

  @Override
  public ElementNotFound createElementNotFoundError(Condition condition, Throwable lastError) {
    if (collection.getElements().isEmpty()) {
      return new ElementNotFound(collection.driver(), collection.description(), visible, lastError);
    }
    return super.createElementNotFoundError(condition, lastError);
  }

  @Override
  public String toString() {
    return getSearchCriteria();
  }
}
