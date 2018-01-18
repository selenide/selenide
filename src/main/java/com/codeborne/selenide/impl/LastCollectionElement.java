package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.openqa.selenium.StaleElementReferenceException;
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
  public WebElement getWebElement() {
    try {
      WebElement el = lastElementOf(collection.getElements());
      el.isEnabled(); // check staleness

      return el;
    } catch (StaleElementReferenceException | IndexOutOfBoundsException e) {
      return lastElementOf(collection.getActualElements());
    }
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
