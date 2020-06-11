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
import java.util.List;

@ParametersAreNonnullByDefault
public class CollectionElementByCondition extends WebElementSource {

  @CheckReturnValue
  @Nonnull
  public static SelenideElement wrap(WebElementsCollection collection, Condition condition) {
    return (SelenideElement) Proxy.newProxyInstance(
        collection.getClass().getClassLoader(), new Class<?>[]{SelenideElement.class},
        new SelenideElementProxy(new CollectionElementByCondition(collection, condition)));
  }

  private final WebElementsCollection collection;
  private final Condition condition;

  CollectionElementByCondition(WebElementsCollection collection, Condition condition) {
    this.collection = collection;
    this.condition = condition;
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
    List<WebElement> list = collection.getElements();

    for (WebElement element : list) {
      if (condition.apply(driver(), element)) {
        return element;
      }
    }

    throw new ElementNotFound(driver(), getSearchCriteria(), condition);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String getSearchCriteria() {
    return collection.description() + ".findBy(" + condition + ")";
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return getSearchCriteria();
  }
}
