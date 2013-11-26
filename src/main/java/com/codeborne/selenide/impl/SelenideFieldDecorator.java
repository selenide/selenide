package com.codeborne.selenide.impl;

import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.Annotations;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SelenideFieldDecorator extends DefaultFieldDecorator {
  private final SearchContext searchContext;

  public SelenideFieldDecorator(SearchContext searchContext) {
    super(new DefaultElementLocatorFactory(searchContext));
    this.searchContext = searchContext;
  }

  @Override
  public Object decorate(ClassLoader loader, Field field) {
    By selector = new Annotations(field).buildBy();
    if (WebElement.class.isAssignableFrom(field.getType())) {
      return WaitingSelenideElement.wrap(searchContext, selector, 0);
    } else if (ElementsContainer.class.isAssignableFrom(field.getType())) {
      return createElementsContainer(selector, field);
    } else if (isDecoratableList(field, ElementsContainer.class)) {
      return createElementsContainerList(field, selector);
    } else if (isDecoratableList(field, SelenideElement.class)) {
      return SelenideElementListProxy.wrap(factory.createLocator(field));
    }

    return super.decorate(loader, field);
  }

  private List<ElementsContainer> createElementsContainerList(Field field, By selector) {
    try {
      List<ElementsContainer> result = new ArrayList<ElementsContainer>();
      Class<?> listType = getListGenericType(field);
      List<SelenideElement> selfList = SelenideElementListProxy.wrap(factory.createLocator(field));
      for (SelenideElement element : selfList) {
        result.add(initElementsContainer(listType, element));
      }
      return result;  //To change body of created methods use File | Settings | File Templates.
    } catch (Exception e) {
      throw new RuntimeException("Failed to create elements container list for field " + field.getName(), e);
    }
  }

  private ElementsContainer createElementsContainer(By selector, Field field) {
    try {
      SelenideElement self = WaitingSelenideElement.wrap(searchContext, selector, 0);
      return initElementsContainer(field.getType(), self);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create elements container for field " + field.getName(), e);
    }
  }

  private ElementsContainer initElementsContainer(Class<?> type, SelenideElement self) throws InstantiationException, IllegalAccessException {
    ElementsContainer result = (ElementsContainer) type.newInstance();
    PageFactory.initElements(new SelenideFieldDecorator(self), result);
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
