package com.codeborne.selenide.impl;

import com.codeborne.selenide.DOM;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.ShouldableWebElement;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;

import java.lang.reflect.*;
import java.util.List;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class ExtendedFieldDecorator extends DefaultFieldDecorator {
  public ExtendedFieldDecorator(SearchContext searchContext) {
    super(new DefaultElementLocatorFactory(searchContext));
  }

  @Override
  public Object decorate(ClassLoader loader, Field field) {
    if (ShouldableWebElement.class.isAssignableFrom(field.getType())) {
      return ShouldableWebElementProxy.wrap(factory.createLocator(field));
    } else if (ElementsContainer.class.isAssignableFrom(field.getType())) {
      return createElementsContainer(field);
    } else if (isDecoratableList(field)) {
      return ShouldableWebElementListProxy.wrap(factory.createLocator(field));
    }
    return super.decorate(loader, field);
  }

  private ElementsContainer createElementsContainer(Field field) {
    try {
      ShouldableWebElement self = ShouldableWebElementProxy.wrap(factory.createLocator(field));
      ElementsContainer result = (ElementsContainer) field.getType().newInstance();
      PageFactory.initElements(new ExtendedFieldDecorator(self), result);
      result.setSelf(self);
      return result;
    } catch (Exception e) {
      throw new RuntimeException("Failed to create elements container for field " + field.getName(), e);
    }
  }

  private boolean isDecoratableList(Field field) {
    if (!List.class.isAssignableFrom(field.getType())) {
      return false;
    }

    Type genericType = field.getGenericType();
    if (!(genericType instanceof ParameterizedType)) {
      return false;
    }

    Type listType = ((ParameterizedType) genericType).getActualTypeArguments()[0];

    if (!ShouldableWebElement.class.equals(listType)) {
      return false;
    }

    if (field.getAnnotation(FindBy.class) == null &&
        field.getAnnotation(FindBys.class) == null) {
      return false;
    }

    return true;
  }

}
