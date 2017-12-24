package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;
import java.util.List;

import static com.codeborne.selenide.Condition.visible;

public class CollectionElement extends WebElementSource {
  public static SelenideElement wrap(WebElementsCollection collection, List<WebElement> actualElements, int index) {
    return (SelenideElement) Proxy.newProxyInstance(
        collection.getClass().getClassLoader(), new Class<?>[]{SelenideElement.class},
        new SelenideElementProxy(new CollectionElement(collection, actualElements, index)));
  }

  private final WebElementsCollection collection;
  private List<WebElement> actualElements;
  private final int index;

  CollectionElement(WebElementsCollection collection, List<WebElement> actualElements, int index) {
    this.collection = collection;
    this.actualElements = actualElements;
    this.index = index;
  }

  @Override
  public WebElement getWebElement() {
    try {
      WebElement el = actualElements.get(index);
      el.isEnabled(); // check staleness

      return el;
    } catch (StaleElementReferenceException | IndexOutOfBoundsException e) {
      actualElements = collection.getActualElements();
      return actualElements.get(index);
    }
  }

  @Override
  public String getSearchCriteria() {
    return collection.description() + '[' + index  + ']';
  }

  @Override
  public ElementNotFound createElementNotFoundError(Condition condition, Throwable lastError) {
    if (collection.getActualElements().isEmpty()) {
      return new ElementNotFound(collection.description(), visible, lastError);
    }
    return super.createElementNotFoundError(condition, lastError);
  }

  @Override
  public String toString() {
    return getSearchCriteria();
  }
}
