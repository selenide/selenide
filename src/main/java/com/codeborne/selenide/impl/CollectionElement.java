package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Proxy;

import static com.codeborne.selenide.Condition.visible;

@ParametersAreNonnullByDefault
public class CollectionElement extends WebElementSource {

  @CheckReturnValue
  @Nonnull
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
  @CheckReturnValue
  @Nonnull
  public Driver driver() {
    return collection.driver();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebElement getWebElement() {
    return collection.getElement(index);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String getSearchCriteria() {
    return collection.description() + '[' + index  + ']';
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public ElementNotFound createElementNotFoundError(Condition condition, Throwable lastError) {
    if (collection.getElements().isEmpty()) {
      return new ElementNotFound(collection.driver(), getSearchCriteria(), visible, lastError);
    }
    return super.createElementNotFoundError(condition, lastError);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return getSearchCriteria();
  }
}
