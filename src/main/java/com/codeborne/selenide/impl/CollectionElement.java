package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.ex.ElementNotFound;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;

import static com.codeborne.selenide.Condition.visible;

public class CollectionElement extends WebElementSource {

  public static SelenideElement wrap(CollectionSource collection, int index) {
    return wrap(SelenideElement.class, collection, index);
  }

  @SuppressWarnings("unchecked")
  public static <T extends SelenideElement> T wrap(Class<T> clazz, CollectionSource collection, int index) {
    return (T) Proxy.newProxyInstance(
      collection.getClass().getClassLoader(), new Class<?>[]{clazz},
      new SelenideElementProxy<>(new CollectionElement(collection, index)));
  }

  private final CollectionSource collection;
  private final int index;

  CollectionElement(CollectionSource collection, int index) {
    this.collection = collection;
    this.index = index;
  }

  @Override
  public Driver driver() {
    return collection.driver();
  }

  @Override
  public WebElement getWebElement() {
    return collection.getElement(index);
  }

  @Override
  public String getSearchCriteria() {
    return collection.getSearchCriteria() + '[' + index + ']';
  }

  @Override
  public String description() {
    return getAlias().getOrElse(() -> collection.shortDescription() + '[' + index + ']');
  }

  @Override
  public ElementNotFound createElementNotFoundError(WebElementCondition condition, @Nullable Throwable cause) {
    if (collection.getElements().isEmpty()) {
      return new ElementNotFound(getAlias(), getSearchCriteria(), visible, cause);
    }
    return super.createElementNotFoundError(condition, cause);
  }
}
