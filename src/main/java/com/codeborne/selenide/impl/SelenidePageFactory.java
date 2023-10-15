package com.codeborne.selenide.impl;

import com.codeborne.selenide.As;
import com.codeborne.selenide.Container;
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
@SuppressWarnings("ChainOfInstanceofChecks")
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
    if (pageObject instanceof ElementsContainer) {
      throw new IllegalArgumentException("Page object should not be marked as ElementsContainer");
    }
    initElements(driver, null, pageObject, types);
    return pageObject;
  }

  /**
   * Similar to the other "initElements" methods, but takes an {@link FieldDecorator} which is used
   * for decorating each of the fields.
   *
   * @param page The object to decorate the fields of
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
      initField(driver, searchContext, page, genericTypes, field);
    }
  }

  protected void initField(Driver driver, @Nullable WebElementSource searchContext, Object page, Type[] genericTypes, Field field) {
    Object value = createFieldValue(driver, searchContext, page, genericTypes, field);
    if (value != null) {
      setFieldValue(page, field, value);
    }
  }

  @Nullable
  @CheckReturnValue
  protected Object createFieldValue(Driver driver, @Nullable WebElementSource searchContext,
                                    Object page, Type[] genericTypes, Field field) {
    Object fieldValue = getFieldValue(page, field);
    if (fieldValue == null) {
      By selector = findSelector(driver, field);
      return decorate(page.getClass().getClassLoader(), driver, searchContext, field, selector, genericTypes);
    }
    As as = field.getAnnotation(As.class);
    if (as != null && fieldValue instanceof SelenideElement element) {
      return element.as(as.value());
    }
    if (as != null && fieldValue instanceof ElementsCollection collection) {
      return collection.as(as.value());
    }
    return null;
  }

  /**
   * @param driver Used by subclasses (e.g. in selenide-appium plugin)
   * @param field  expected to be an element in a Page Object
   * @return {@link By} instance used by webdriver to locate elements
   */
  @Nonnull
  protected By findSelector(@SuppressWarnings("unused") Driver driver, Field field) {
    return new Annotations(field).buildBy();
  }

  protected boolean shouldCache(Field field) {
    return new Annotations(field).isLookupCached();
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
  @Deprecated
  protected boolean isInitialized(Object page, Field field) {
    return getFieldValue(page, field) != null;
  }

  @CheckReturnValue
  @Nullable
  protected Object getFieldValue(Object page, Field field) {
    try {
      field.setAccessible(true);
      return field.get(page);
    }
    catch (IllegalAccessException e) {
      throw new PageObjectException("Failed to access field " + field + " in " + page, e);
    }
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Container createElementsContainer(Driver driver, @Nullable WebElementSource searchContext, Field field, By selector) {
    try {
      WebElementSource self = new ElementFinder(driver, searchContext, selector, 0);
      if (shouldCache(field)) {
        self = new LazyWebElementSnapshot(self);
      }
      return initElementsContainer(driver, field, self);
    }
    catch (ReflectiveOperationException e) {
      throw new PageObjectException("Failed to create elements container for field " + field.getName(), e);
    }
  }

  @CheckReturnValue
  @Nonnull
  Container initElementsContainer(Driver driver, Field field, WebElementSource self) throws ReflectiveOperationException {
    Type[] genericTypes = field.getGenericType() instanceof ParameterizedType parameterizedType ?
      parameterizedType.getActualTypeArguments() : new Type[0];
    return initElementsContainer(driver, field, self, field.getType(), genericTypes);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Container initElementsContainer(Driver driver,
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
    Container result = (Container) constructor.newInstance();
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

  @Nullable
  @CheckReturnValue
  private String alias(Field field) {
    As alias = field.getAnnotation(As.class);
    return alias == null ? null : alias.value();
  }

  @CheckReturnValue
  @Nullable
  public Object decorate(ClassLoader loader,
                         Driver driver, @Nullable WebElementSource searchContext,
                         Field field, By selector, Type[] genericTypes) {
    String alias = alias(field);
    if (ElementsContainer.class.equals(field.getDeclaringClass()) && "self".equals(field.getName())) {
      return decorateElementsContainer(searchContext, field);
    }
    if (WebElement.class.isAssignableFrom(field.getType())) {
      return decorateWebElement(driver, searchContext, selector, field, alias);
    }
    if (ElementsCollection.class.isAssignableFrom(field.getType()) ||
      isDecoratableList(field, genericTypes, WebElement.class)) {
      return createElementsCollection(driver, searchContext, selector, field, alias);
    }
    else if (Container.class.isAssignableFrom(field.getType())) {
      return createElementsContainer(driver, searchContext, field, selector);
    }
    else if (isDecoratableList(field, genericTypes, Container.class)) {
      return createElementsContainerList(driver, searchContext, field, genericTypes, selector);
    }

    return defaultFieldDecorator(driver, searchContext).decorate(loader, field);
  }

  @Nonnull
  private static SelenideElement decorateElementsContainer(@Nullable WebElementSource searchContext, Field field) {
    if (searchContext != null) {
      return ElementFinder.wrap(SelenideElement.class, searchContext);
    }
    else {
      String message = String.format("Cannot initialize field %s.%s: it's not bound to any page object",
        field.getDeclaringClass().getSimpleName(), field.getName());
      throw new IllegalArgumentException(message);
    }
  }

  @Nonnull
  protected SelenideElement decorateWebElement(Driver driver, @Nullable WebElementSource searchContext, By selector,
                                               Field field, @Nullable String alias) {
    return shouldCache(field) ?
      LazyWebElementSnapshot.wrap(new ElementFinder(driver, searchContext, selector, 0, alias)) :
      ElementFinder.wrap(driver, SelenideElement.class, searchContext, selector, 0, alias);
  }

  @Nonnull
  protected ElementsCollection createElementsCollection(Driver driver, @Nullable WebElementSource searchContext,
                                                        By selector, Field field, @Nullable String alias) {
    CollectionSource collection = new BySelectorCollection(driver, searchContext, selector);
    if (alias != null) {
      collection.setAlias(alias);
    }
    if (shouldCache(field)) {
      collection = new LazyCollectionSnapshot(collection);
    }
    return new ElementsCollection(collection);
  }

  @CheckReturnValue
  @Nonnull
  protected DefaultFieldDecorator defaultFieldDecorator(Driver driver, @Nullable WebElementSource searchContext) {
    SearchContext context = searchContext == null ? driver.getWebDriver() : searchContext.getWebElement();
    return new DefaultFieldDecorator(new DefaultElementLocatorFactory(context));
  }

  @CheckReturnValue
  @Nonnull
  protected List<Container> createElementsContainerList(Driver driver, @Nullable WebElementSource searchContext,
                                                        Field field, Type[] genericTypes, By selector) {
    Class<?> listType = getListGenericType(field, genericTypes);
    if (listType == null) {
      throw new IllegalArgumentException("Cannot detect list type for " + field);
    }
    CollectionSource collection = new BySelectorCollection(driver, searchContext, selector);
    if (shouldCache(field)) {
      collection = new LazyCollectionSnapshot(collection);
    }
    return new ElementsContainerCollection(this, driver, field, listType, genericTypes, collection);
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
    else if (firstType instanceof Class<?> classType) {
      return classType;
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
