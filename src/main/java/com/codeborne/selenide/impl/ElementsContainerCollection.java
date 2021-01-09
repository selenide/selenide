package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.PageObjectException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.AbstractList;

import static com.codeborne.selenide.Condition.exist;

@ParametersAreNonnullByDefault
class ElementsContainerCollection extends AbstractList<ElementsContainer> {
  private final PageObjectFactory pageFactory;
  private final Driver driver;
  private final SearchContext parent;
  private final Field field;
  private final Class<?> listType;
  private final Type[] genericTypes;
  private final By selector;

  ElementsContainerCollection(PageObjectFactory pageFactory, Driver driver, SearchContext parent,
                              Field field, Class<?> listType, Type[] genericTypes, By selector) {
    this.pageFactory = pageFactory;
    this.driver = driver;
    this.parent = parent;
    this.field = field;
    this.listType = listType;
    this.genericTypes = genericTypes;
    this.selector = selector;
  }

  @CheckReturnValue
  @Nonnull
  @Override
  public ElementsContainer get(int index) {
    SelenideElement self = ElementFinder.wrap(driver, parent, selector, index);
    try {
      return pageFactory.initElementsContainer(driver, field, self, listType, genericTypes);
    }
    catch (ReflectiveOperationException e) {
      throw new PageObjectException("Failed to initialize field " + field, e);
    }
  }

  @CheckReturnValue
  @Override
  public int size() {
    try {
      return WebElementSelector.instance.findElements(driver, parent, selector).size();
    }
    catch (NoSuchElementException e) {
      throw new ElementNotFound(driver, selector.toString(), exist, e);
    }
  }
}
