package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.ex.ElementNotFound;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;
import java.util.List;

import static com.codeborne.selenide.Condition.visible;

public class LastCollectionElement extends WebElementSource {
  public static SelenideElement wrap(CollectionSource collection) {
    return (SelenideElement) Proxy.newProxyInstance(
      collection.getClass().getClassLoader(), new Class<?>[]{SelenideElement.class},
      new SelenideElementProxy<>(new LastCollectionElement(collection)));
  }

  private final CollectionSource collection;

  LastCollectionElement(CollectionSource collection) {
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
    return collection.getSearchCriteria() + ":last";
  }

  @Override
  public String description() {
    return getAlias().getOrElse(() -> collection.shortDescription() + ":last");
  }

  @Override
  public ElementNotFound createElementNotFoundError(WebElementCondition condition, @Nullable Throwable cause) {
    if (collection.getElements().isEmpty()) {
      return new ElementNotFound(getAlias(), getSearchCriteria(), visible, cause);
    }
    return super.createElementNotFoundError(condition, cause);
  }
}
