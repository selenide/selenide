package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.PageObjectException;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.Annotations;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Factory class to make using Page Objects simpler and easier.
 *
 * @see <a href="https://github.com/SeleniumHQ/selenium/wiki/PageObjects">Page Objects Wiki</a>
 */
@ParametersAreNonnullByDefault
public class SelenidePageFactory implements PageObjectFactory {
  @Override
  @CheckReturnValue
  @Nonnull
  public <PageObjectClass> PageObjectClass page(Driver driver, Class<PageObjectClass> pageObjectClass) {
    try {
      Constructor<PageObjectClass> constructor = pageObjectClass.getDeclaredConstructor();
      constructor.setAccessible(true);
      return page(driver, constructor.newInstance());
    }
    catch (ReflectiveOperationException e) {
      throw new PageObjectException("Failed to create new instance of " + pageObjectClass, e);
    }
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public <PageObjectClass, T extends PageObjectClass> PageObjectClass page(Driver driver, T pageObject) {
    Type[] types = pageObject.getClass().getGenericInterfaces();
    initElements(driver, decorator(driver, driver.getWebDriver()), pageObject, types);
    return pageObject;
  }

  @CheckReturnValue
  @Nonnull
  protected SelenideFieldDecorator decorator(Driver driver, SearchContext searchContext) {
    return new SelenideFieldDecorator(this, driver, searchContext);
  }

  /**
   * Similar to the other "initElements" methods, but takes an {@link FieldDecorator} which is used
   * for decorating each of the fields.
   *
   * @param decorator the decorator to use
   * @param page      The object to decorate the fields of
   */
  public void initElements(Driver driver, SelenideFieldDecorator decorator, Object page, Type[] genericTypes) {
    Class<?> proxyIn = page.getClass();
    while (proxyIn != Object.class) {
      initFields(driver, decorator, page, proxyIn, genericTypes);
      proxyIn = proxyIn.getSuperclass();
    }
  }

  protected void initFields(Driver driver, SelenideFieldDecorator decorator, Object page, Class<?> proxyIn, Type[] genericTypes) {
    Field[] fields = proxyIn.getDeclaredFields();
    for (Field field : fields) {
      if (!isInitialized(page, field)) {
        By selector = findSelector(driver, field);
        Object value = decorator.decorate(page.getClass().getClassLoader(), field, selector, genericTypes);
        if (value != null) {
          setFieldValue(page, field, value);
        }
      }
    }
  }

  @Nonnull
  protected By findSelector(Driver driver, Field field) {
    return new Annotations(field).buildBy();
  }

  protected void setFieldValue(Object page, Field field, Object value) {
    try {
      field.setAccessible(true);
      field.set(page, value);
    }
    catch (IllegalAccessException e) {
      throw new PageObjectException("Failed to assign field " + field + " to value " + value, e);
    }
  }

  @CheckReturnValue
  protected boolean isInitialized(Object page, Field field) {
    try {
      field.setAccessible(true);
      return field.get(page) != null;
    }
    catch (IllegalAccessException e) {
      throw new PageObjectException("Failed to access field " + field + " in " + page, e);
    }
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public ElementsContainer createElementsContainer(Driver driver, SearchContext searchContext, Field field, By selector) {
    try {
      SelenideElement self = ElementFinder.wrap(driver, searchContext, selector, 0);
      return initElementsContainer(driver, field, self);
    }
    catch (ReflectiveOperationException e) {
      throw new PageObjectException("Failed to create elements container for field " + field.getName(), e);
    }
  }

  @CheckReturnValue
  @Nonnull
  ElementsContainer initElementsContainer(Driver driver, Field field, SelenideElement self) throws ReflectiveOperationException {
    Type[] genericTypes = field.getGenericType() instanceof ParameterizedType ?
      ((ParameterizedType) field.getGenericType()).getActualTypeArguments() : new Type[0];
    return initElementsContainer(driver, field, self, field.getType(), genericTypes);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public ElementsContainer initElementsContainer(Driver driver,
                                                 Field field,
                                                 SelenideElement self,
                                                 Class<?> type,
                                                 Type[] genericTypes) throws ReflectiveOperationException {
    if (Modifier.isInterface(type.getModifiers())) {
      throw new IllegalArgumentException("Cannot initialize field " + field + ": " + type + " is interface");
    }
    if (Modifier.isAbstract(type.getModifiers())) {
      throw new IllegalArgumentException("Cannot initialize field " + field + ": " + type + " is abstract");
    }
    Constructor<?> constructor = type.getDeclaredConstructor();
    constructor.setAccessible(true);
    ElementsContainer result = (ElementsContainer) constructor.newInstance();
    initElements(driver, decorator(driver, self), result, genericTypes);
    return result;
  }
}
