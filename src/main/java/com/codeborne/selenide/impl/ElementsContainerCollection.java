package com.codeborne.selenide.impl;

import com.codeborne.selenide.Container;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.PageObjectException;
import com.codeborne.selenide.ex.UIAssertionError;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.NoSuchElementException;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.AbstractList;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.impl.Alias.NONE;
import static com.codeborne.selenide.impl.SelenideAnnotations.isShadowRoot;

public class ElementsContainerCollection<T extends Container> extends AbstractList<T> {
  private final PageObjectFactory pageFactory;
  private final Driver driver;
  @Nullable
  private final Field field;
  private final Class<T> listType;
  private final Type[] genericTypes;
  private final CollectionSource collection;

  public ElementsContainerCollection(PageObjectFactory pageFactory,
                                     Driver driver,
                                     @Nullable Field field,
                                     Class<T> listType,
                                     Type[] genericTypes,
                                     CollectionSource collection) {
    this.pageFactory = pageFactory;
    this.driver = driver;
    this.field = field;
    this.listType = listType;
    this.genericTypes = genericTypes;
    this.collection = collection;
  }

  @Override
  public T get(int index) {
    String searchCriteria = String.format("%s[%s]", collection.getSearchCriteria(), index);
    WebElementSource self = new WebElementWrapper(driver, collection.getElement(index), searchCriteria, isShadowRoot(field, listType));
    try {
      return initElementsContainer(self);
    } catch (ReflectiveOperationException e) {
      throw pageObjectException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private T initElementsContainer(WebElementSource self) throws ReflectiveOperationException {
    return (T) pageFactory.initElementsContainer(driver, field, self, listType, genericTypes);
  }

  private PageObjectException pageObjectException(ReflectiveOperationException e) {
    String message = field == null
      ? "Failed to initialize type " + listType.getName()
      : "Failed to initialize field " + field;

    return new PageObjectException(message, e);
  }

  @Override
  public int size() {
    try {
      return collection.getElements().size();
    } catch (NoSuchElementException e) {
      throw UIAssertionError.wrap(driver, new ElementNotFound(NONE, collection.getSearchCriteria(), exist, e), 0L);
    }
  }

  @Override
  public String toString() {
    return collection.description();
  }
}
