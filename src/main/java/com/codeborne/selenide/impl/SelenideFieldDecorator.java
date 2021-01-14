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
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
public class SelenideFieldDecorator {
  private static final Logger logger = LoggerFactory.getLogger(SelenideFieldDecorator.class);
  private final FieldDecorator defaultFieldDecorator;
  private final PageObjectFactory pageFactory;
  private final Driver driver;
  private final SearchContext searchContext;

  protected SelenideFieldDecorator(PageObjectFactory pageFactory, Driver driver, SearchContext searchContext) {
    defaultFieldDecorator = new DefaultFieldDecorator(new DefaultElementLocatorFactory(searchContext));
    this.pageFactory = pageFactory;
    this.driver = driver;
    this.searchContext = searchContext;
  }

  @CheckReturnValue
  @Nullable
  public final Object decorate(ClassLoader loader, Field field, By selector) {
    Type[] classGenericTypes = field.getDeclaringClass().getGenericInterfaces();
    return decorate(loader, field, selector, classGenericTypes);
  }

  @CheckReturnValue
  @Nullable
  public Object decorate(ClassLoader loader, Field field, By selector, Type[] genericTypes) {
    if (ElementsContainer.class.equals(field.getDeclaringClass()) && "self".equals(field.getName())) {
      if (searchContext instanceof SelenideElement) {
        return searchContext;
      }
      else {
        logger.warn("Cannot initialize field {}", field);
        return null;
      }
    }
    if (WebElement.class.isAssignableFrom(field.getType())) {
      return ElementFinder.wrap(driver, searchContext, selector, 0);
    }
    if (ElementsCollection.class.isAssignableFrom(field.getType()) ||
      isDecoratableList(field, genericTypes, WebElement.class)) {
      return new ElementsCollection(new BySelectorCollection(driver, searchContext, selector));
    }
    else if (ElementsContainer.class.isAssignableFrom(field.getType())) {
      return pageFactory.createElementsContainer(driver, searchContext, field, selector);
    }
    else if (isDecoratableList(field, genericTypes, ElementsContainer.class)) {
      return createElementsContainerList(field, genericTypes, selector);
    }

    return defaultFieldDecorator.decorate(loader, field);
  }

  @CheckReturnValue
  @Nonnull
  protected List<ElementsContainer> createElementsContainerList(Field field, Type[] genericTypes, By selector) {
    Class<?> listType = getListGenericType(field, genericTypes);
    if (listType == null) {
      throw new IllegalArgumentException("Cannot detect list type for " + field);
    }

    return new ElementsContainerCollection(pageFactory, driver, searchContext, field, listType, genericTypes, selector);
  }

  @CheckReturnValue
  protected boolean isDecoratableList(Field field, Type[] genericTypes, Class<?> type) {
    if (!List.class.isAssignableFrom(field.getType())) {
      return false;
    }

    Class<?> listType = getListGenericType(field, genericTypes);

    return listType != null && type.isAssignableFrom(listType)
      && (field.getAnnotation(FindBy.class) != null || field.getAnnotation(FindBys.class) != null);
  }

  @CheckReturnValue
  @Nullable
  protected Class<?> getListGenericType(Field field, Type[] genericTypes) {
    Type fieldType = field.getGenericType();
    if (!(fieldType instanceof ParameterizedType)) return null;

    Type[] actualTypeArguments = ((ParameterizedType) fieldType).getActualTypeArguments();
    Type firstType = actualTypeArguments[0];
    if (firstType instanceof TypeVariable) {
      int indexOfType = indexOf(field.getDeclaringClass(), firstType);
      return (Class<?>) genericTypes[indexOfType];
    }
    else if (firstType instanceof Class) {
      return (Class<?>) firstType;
    }
    throw new IllegalArgumentException("Cannot detect list type of " + field);
  }

  protected int indexOf(Class<?> klass, Type firstArgument) {
    Object[] objects = Arrays.stream(klass.getTypeParameters()).toArray();
    for (int i = 0; i < objects.length; i++) {
      if (objects[i].equals(firstArgument)) return i;
    }
    return -1;
  }
}
