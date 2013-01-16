package com.codeborne.selenide.impl;

import com.codeborne.selenide.ShouldableWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;

import java.lang.reflect.*;
import java.util.List;

public class ExtendedFieldDecorator extends DefaultFieldDecorator {
  public ExtendedFieldDecorator(WebDriver webDriver) {
    super(new DefaultElementLocatorFactory(webDriver));
  }

  @Override
  public Object decorate(ClassLoader loader, Field field) {
    if (ShouldableWebElement.class.isAssignableFrom(field.getType())) {
      return ShouldableWebElementProxy.wrap(factory.createLocator(field));
    } else if (isDecoratableList(field)) {
      return ShouldableWebElementListProxy.wrap(factory.createLocator(field));
    }
    return super.decorate(loader, field);
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
