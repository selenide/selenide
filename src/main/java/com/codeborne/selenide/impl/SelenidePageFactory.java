package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.PageObjectException;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.pagefactory.Annotations;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
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
import java.util.Arrays;
import java.util.List;

/**
 * Factory class to make using Page Objects simpler and easier.
 *
 * @see <a href="https://github.com/SeleniumHQ/selenium/wiki/PageObjects">Page Objects Wiki</a>
 */
@ParametersAreNonnullByDefault
public class SelenidePageFactory implements PageObjectFactory {
  private static final Logger logger = LoggerFactory.getLogger(SelenidePageFactory.class);

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
    initElements(driver, null, pageObject, types);
    return pageObject;
  }

  /**
   * Similar to the other "initElements" methods, but takes an {@link FieldDecorator} which is used
   * for decorating each of the fields.
   *
   * @param page      The object to decorate the fields of
   */
  public void initElements(Driver driver, @Nullable WebElementSource searchContext, Object page, Type[] genericTypes) {
    Class<?> proxyIn = page.getClass();
    while (proxyIn != Object.class) {
      initFields(driver, searchContext, page, proxyIn, genericTypes);
      proxyIn = proxyIn.getSuperclass();
    }
  }

  protected void initFields(Driver driver, @Nullable WebElementSource searchContext,
                            Object page, Class<?> proxyIn, Type[] genericTypes) {
    Field[] fields = proxyIn.getDeclaredFields();
    for (Field field : fields) {
      if (!isInitialized(page, field)) {
        By selector = findSelector(driver, field);
        Object value = decorate(page.getClass().getClassLoader(), driver, searchContext, field, selector, genericTypes);
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
  public ElementsContainer createElementsContainer(Driver driver, @Nullable WebElementSource searchContext, Field field, By selector) {
    try {
      WebElementSource self = new ElementFinder(driver, searchContext, selector, 0);
      return initElementsContainer(driver, field, self);
    }
    catch (ReflectiveOperationException e) {
      throw new PageObjectException("Failed to create elements container for field " + field.getName(), e);
    }
  }

  @CheckReturnValue
  @Nonnull
  ElementsContainer initElementsContainer(Driver driver, Field field, WebElementSource self) throws ReflectiveOperationException {
    Type[] genericTypes = field.getGenericType() instanceof ParameterizedType ?
      ((ParameterizedType) field.getGenericType()).getActualTypeArguments() : new Type[0];
    return initElementsContainer(driver, field, self, field.getType(), genericTypes);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public ElementsContainer initElementsContainer(Driver driver,
                                                 Field field,
                                                 WebElementSource self,
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
    initElements(driver, self, result, genericTypes);
    return result;
  }


  @CheckReturnValue
  @Nullable
  public final Object decorate(ClassLoader loader,
                               Driver driver, @Nullable WebElementSource searchContext,
                               Field field, By selector) {
    Type[] classGenericTypes = field.getDeclaringClass().getGenericInterfaces();
    return decorate(loader, driver, searchContext, field, selector, classGenericTypes);
  }

  @CheckReturnValue
  @Nullable
  public Object decorate(ClassLoader loader,
                         Driver driver, @Nullable WebElementSource searchContext,
                         Field field, By selector, Type[] genericTypes) {
    if (ElementsContainer.class.equals(field.getDeclaringClass()) && "self".equals(field.getName())) {
      if (searchContext != null) {
        return ElementFinder.wrap(SelenideElement.class, searchContext);
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
      return createElementsContainer(driver, searchContext, field, selector);
    }
    else if (isDecoratableList(field, genericTypes, ElementsContainer.class)) {
      return createElementsContainerList(driver, searchContext, field, genericTypes, selector);
    }

    return defaultFieldDecorator(driver, searchContext).decorate(loader, field);
  }

  @CheckReturnValue
  @Nonnull
  protected DefaultFieldDecorator defaultFieldDecorator(Driver driver, @Nullable WebElementSource searchContext) {
    SearchContext context = searchContext == null ? driver.getWebDriver() : searchContext.getWebElement();
    return new DefaultFieldDecorator(new DefaultElementLocatorFactory(context));
  }

  @CheckReturnValue
  @Nonnull
  protected List<ElementsContainer> createElementsContainerList(Driver driver, @Nullable WebElementSource searchContext,
                                                                Field field, Type[] genericTypes, By selector) {
    Class<?> listType = getListGenericType(field, genericTypes);
    if (listType == null) {
      throw new IllegalArgumentException("Cannot detect list type for " + field);
    }

    return new ElementsContainerCollection(this, driver, searchContext, field, listType, genericTypes, selector);
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
