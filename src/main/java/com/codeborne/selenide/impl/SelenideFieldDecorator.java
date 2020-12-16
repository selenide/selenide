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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
public class SelenideFieldDecorator extends DefaultFieldDecorator {
  private static final Logger logger = LoggerFactory.getLogger(SelenideFieldDecorator.class);
  private final SelenidePageFactory pageFactory;
  private final Driver driver;
  private final SearchContext searchContext;

  public SelenideFieldDecorator(SelenidePageFactory pageFactory, Driver driver, SearchContext searchContext) {
    super(new DefaultElementLocatorFactory(searchContext));
    this.pageFactory = pageFactory;
    this.driver = driver;
    this.searchContext = searchContext;
  }

  @CheckReturnValue
  @Nullable
  @Override
  public final Object decorate(ClassLoader loader, Field field) {
    Type[] classGenericTypes = field.getDeclaringClass().getGenericInterfaces();
    return decorate(loader, field, classGenericTypes);
  }

  @CheckReturnValue
  @Nullable
  public final Object decorate(ClassLoader loader, Field field, Type[] genericTypes) {
    if (ElementsContainer.class.equals(field.getDeclaringClass()) && "self".equals(field.getName())) {
      if (searchContext instanceof SelenideElement) {
        return searchContext;
      }
      else {
        logger.warn("Cannot initialize field {}", field);
        return null;
      }
    }
    By selector = new Annotations(field).buildBy();
    if (WebElement.class.isAssignableFrom(field.getType())) {
      return ElementFinder.wrap(driver, searchContext, selector, 0);
    }
    if (ElementsCollection.class.isAssignableFrom(field.getType()) || isDecoratableList(field, genericTypes, WebElement.class)) {
      return new ElementsCollection(new BySelectorCollection(driver, searchContext, selector));
    }
    else if (ElementsContainer.class.isAssignableFrom(field.getType())) {
      return createElementsContainer(selector, field);
    }
    else if (isDecoratableList(field, genericTypes, ElementsContainer.class)) {
      return createElementsContainerList(field, genericTypes);
    }

    return super.decorate(loader, field);
  }

  @CheckReturnValue
  @Nonnull
  private List<ElementsContainer> createElementsContainerList(Field field, Type[] genericTypes) {
    List<ElementsContainer> result = new ArrayList<>();
    Class<?> listType = getListGenericType(field, genericTypes);
    if (listType == null) {
      throw new IllegalArgumentException("Cannot detect list type for " + field);
    }

    try {
      List<SelenideElement> selfList = SelenideElementListProxy.wrap(driver, factory.createLocator(field));
      for (SelenideElement element : selfList) {
        Type[] types = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
        result.add(initElementsContainer(field, element, listType, types));
      }
      return result;
    }
    catch (RuntimeException e) {
      throw e;
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to create elements container list for field " + field.getName(), e);
    }
  }

  @CheckReturnValue
  @Nonnull
  private ElementsContainer createElementsContainer(By selector, Field field) {
    try {
      SelenideElement self = ElementFinder.wrap(driver, searchContext, selector, 0);
      return initElementsContainer(field, self);
    }
    catch (RuntimeException e) {
      throw e;
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to create elements container for field " + field.getName(), e);
    }
  }

  private ElementsContainer initElementsContainer(Field field, SelenideElement self) throws Exception {
    Type[] genericTypes = field.getGenericType() instanceof ParameterizedType ?
      ((ParameterizedType) field.getGenericType()).getActualTypeArguments() : new Type[0];
    return initElementsContainer(field, self, field.getType(), genericTypes);
  }

  @CheckReturnValue
  @Nonnull
  private ElementsContainer initElementsContainer(Field field,
                                                  SelenideElement self,
                                                  Class<?> type,
                                                  Type[] genericTypes) throws Exception {
    if (Modifier.isInterface(type.getModifiers())) {
      throw new IllegalArgumentException("Cannot initialize field " + field + ": " + type + " is interface");
    }
    if (Modifier.isAbstract(type.getModifiers())) {
      throw new IllegalArgumentException("Cannot initialize field " + field + ": " + type + " is abstract");
    }
    Constructor<?> constructor = type.getDeclaredConstructor();
    constructor.setAccessible(true);
    ElementsContainer result = (ElementsContainer) constructor.newInstance();
    pageFactory.initElements(new SelenideFieldDecorator(pageFactory, driver, self), result, genericTypes);
    return result;
  }

  @CheckReturnValue
  private boolean isDecoratableList(Field field, Type[] genericTypes, Class<?> type) {
    if (!List.class.isAssignableFrom(field.getType())) {
      return false;
    }

    Class<?> listType = getListGenericType(field, genericTypes);

    return listType != null && type.isAssignableFrom(listType)
      && (field.getAnnotation(FindBy.class) != null || field.getAnnotation(FindBys.class) != null);
  }

  @CheckReturnValue
  @Nullable
  private Class<?> getListGenericType(Field field, Type[] genericTypes) {
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

  private int indexOf(Class<?> klass, Type firstArgument) {
    Object[] objects = Arrays.stream(klass.getTypeParameters()).toArray();
    for (int i = 0; i < objects.length; i++) {
      if (objects[i].equals(firstArgument)) return i;
    }
    return -1;
  }
}
