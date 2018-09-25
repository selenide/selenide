package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;
import java.util.List;

import static com.codeborne.selenide.Condition.visible;

public class LastCollectionElement extends WebElementSource {
  public static SelenideElement wrap(WebElementsCollection collection) {
    return (SelenideElement) Proxy.newProxyInstance(
        collection.getClass().getClassLoader(), new Class<?>[]{SelenideElement.class},
        new SelenideElementProxy(new LastCollectionElement(collection)));
  }

  private final WebElementsCollection collection;

  private LastCollectionElement(WebElementsCollection collection) {
    this.collection = collection;
  }

  @Override
  public Driver driver() {
    return collection.driver();
  }

  @Override
  public WebElement getWebElement() {
    return lastElementOf(collection.getElements());
  }

  private <T> T lastElementOf(List<T> collection) {
    return collection.get(collection.size() - 1);
  }

  @Override
  public String getSearchCriteria() {
    return collection.description() + ".last";
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
