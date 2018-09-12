package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.pagefactory.Annotations;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SelenideFieldDecorator extends DefaultFieldDecorator {
  private final SelenidePageFactory pageFactory;
  private final Driver driver;
  private final SearchContext searchContext;

  public SelenideFieldDecorator(SelenidePageFactory pageFactory, Driver driver, SearchContext searchContext) {
    super(new DefaultElementLocatorFactory(searchContext));
    this.pageFactory = pageFactory;
    this.driver = driver;
    this.searchContext = searchContext;
  }

  @Override
  public Object decorate(ClassLoader loader, Field field) {
    By selector = new Annotations(field).buildBy();
    if (WebElement.class.isAssignableFrom(field.getType())) {
      return ElementFinder.wrap(driver, searchContext, selector, 0);
    }
    if (ElementsCollection.class.isAssignableFrom(field.getType())) {
      return new ElementsCollection(new BySelectorCollection(driver, searchContext, selector));
    }
    else if (ElementsContainer.class.isAssignableFrom(field.getType())) {
      return createElementsContainer(selector, field);
    }
    else if (isDecoratableList(field, ElementsContainer.class)) {
      return createElementsContainerList(field);
    }
    else if (isDecoratableList(field, SelenideElement.class)) {
      return SelenideElementListProxy.wrap(driver, factory.createLocator(field));
    }

    return super.decorate(loader, field);
  }

  private List<ElementsContainer> createElementsContainerList(Field field) {
    try {
      List<ElementsContainer> result = new ArrayList<>();
      Class<?> listType = getListGenericType(field);
      List<SelenideElement> selfList = SelenideElementListProxy.wrap(driver, factory.createLocator(field));
      for (SelenideElement element : selfList) {
        result.add(initElementsContainer(listType, element));
      }
      return result;
    } catch (Exception e) {
      throw new RuntimeException("Failed to create elements container list for field " + field.getName(), e);
    }
  }

  private ElementsContainer createElementsContainer(By selector, Field field) {
    try {
      SelenideElement self = ElementFinder.wrap(driver, searchContext, selector, 0);
      return initElementsContainer(field.getType(), self);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create elements container for field " + field.getName(), e);
    }
  }

  private ElementsContainer initElementsContainer(Class<?> type, SelenideElement self)
      throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    Constructor<?> constructor = type.getDeclaredConstructor();
    constructor.setAccessible(true);
    ElementsContainer result = (ElementsContainer) constructor.newInstance();
    pageFactory.initElements(new SelenideFieldDecorator(pageFactory, driver, self), result);
    result.setSelf(self);
    return result;
  }

  private boolean isDecoratableList(Field field, Class<?> type) {
    if (!List.class.isAssignableFrom(field.getType())) {
      return false;
    }

    Class<?> listType = getListGenericType(field);

    return listType != null && type.isAssignableFrom(listType)
        && (field.getAnnotation(FindBy.class) != null || field.getAnnotation(FindBys.class) != null);
  }

  private Class<?> getListGenericType(Field field) {
    Type genericType = field.getGenericType();
    if (!(genericType instanceof ParameterizedType)) return null;

    return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
  }
}
