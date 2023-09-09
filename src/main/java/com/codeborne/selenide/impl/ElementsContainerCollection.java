package com.codeborne.selenide.impl;

import com.codeborne.selenide.Container;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.PageObjectException;
import org.openqa.selenium.NoSuchElementException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.AbstractList;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.impl.Alias.NONE;

@ParametersAreNonnullByDefault
public class ElementsContainerCollection extends AbstractList<Container> {
  private final PageObjectFactory pageFactory;
  private final Driver driver;
  private final Field field;
  private final Class<?> listType;
  private final Type[] genericTypes;
  private final CollectionSource collection;

  public ElementsContainerCollection(PageObjectFactory pageFactory,
                                     Driver driver,
                                     Field field,
                                     Class<?> listType,
                                     Type[] genericTypes,
                                     CollectionSource collection) {
    this.pageFactory = pageFactory;
    this.driver = driver;
    this.field = field;
    this.listType = listType;
    this.genericTypes = genericTypes;
    this.collection = collection;
  }

  @CheckReturnValue
  @Nonnull
  @Override
  public Container get(int index) {
    String searchCriteria = String.format("%s[%s]", collection.getSearchCriteria(), index);
    WebElementSource self = new WebElementWrapper(driver, collection.getElement(index), searchCriteria);
    try {
      return pageFactory.initElementsContainer(driver, field, self, listType, genericTypes);
    } catch (ReflectiveOperationException e) {
      throw new PageObjectException("Failed to initialize field " + field, e);
    }
  }

  @CheckReturnValue
  @Override
  public int size() {
    try {
      return collection.getElements().size();
    } catch (NoSuchElementException e) {
      throw new ElementNotFound(driver, NONE, collection.getSearchCriteria(), exist, e);
    }
  }
}
