package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
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
  public static SelenideElement wrap(CollectionSource collection, int index) {
    return wrap(SelenideElement.class, collection, index);
  }

  @CheckReturnValue
  @Nonnull
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
    return collection.getSearchCriteria() + '[' + index + ']';
  }

  @Nonnull
  @Override
  public String description() {
    return getAlias().getOrElse(() -> collection.shortDescription() + '[' + index + ']');
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public ElementNotFound createElementNotFoundError(WebElementCondition condition, Throwable cause) {
    if (collection.getElements().isEmpty()) {
      return new ElementNotFound(driver(), getAlias(), getSearchCriteria(), visible, cause);
    }
    return super.createElementNotFoundError(condition, cause);
  }
}
