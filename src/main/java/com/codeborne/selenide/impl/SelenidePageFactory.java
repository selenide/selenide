package com.codeborne.selenide.impl;

import com.codeborne.selenide.As;
import com.codeborne.selenide.BaseElementsCollection;
import com.codeborne.selenide.Container;
import com.codeborne.selenide.Container.ShadowRoot;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.PageObjectException;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
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

  private static final Type[] NO_TYPE = new Type[0];

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
    if (as != null && fieldValue instanceof BaseElementsCollection<?, ?> collection) {
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
    return new SelenideAnnotations(field).buildBy();
  }

  protected boolean shouldCache(Field field) {
    return new SelenideAnnotations(field).isLookupCached();
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
      parameterizedType.getActualTypeArguments() : NO_TYPE;
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

    if (field.isAnnotationPresent(ShadowRoot.class) || type.isAnnotationPresent(ShadowRoot.class)) {
      self.setShadowRoot(true);
    }

    initElements(driver, self, result, genericTypes);
    return result;
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

    if (field.isAnnotationPresent(Container.Self.class)) {
      return injectSelf(searchContext, field);
    }
    if (WebElement.class.isAssignableFrom(field.getType())) {
      return decorateWebElement(driver, searchContext, selector, field, alias);
    }
    if (BaseElementsCollection.class.isAssignableFrom(field.getType())) {
      return createElementsCollection(driver, searchContext, selector, field, alias);
    }

    if (field.getType().isAssignableFrom(ElementsList.class) && isCollectionOfSelenideElements(field, genericTypes)) {
      return createElementsCollection(driver, searchContext, selector, field, alias);
    }
    else if (Container.class.isAssignableFrom(field.getType())) {
      return createElementsContainer(driver, searchContext, field, selector);
    }
    else if (isDecoratableList(field, selector, genericTypes, Container.class)) {
      return createElementsContainerList(driver, searchContext, field, genericTypes, selector);
    }
    else if (isDecoratableList(field, selector, genericTypes, WebElement.class)) {
      return createWebElementsList(loader, driver, searchContext, field);
    }

    return defaultFieldDecorator(driver, searchContext).decorate(loader, field);
  }

  private boolean isCollectionOfSelenideElements(Field field, Type[] genericTypes) {
    Class<?> listGenericType = getListGenericType(field, genericTypes);
    return listGenericType != null && SelenideElement.class.isAssignableFrom(listGenericType);
  }

  @Nonnull
  @CheckReturnValue
  private SelenideElement injectSelf(@Nullable WebElementSource searchContext, Field field) {
    if (searchContext != null) {
      return ElementFinder.wrap(SelenideElement.class, searchContext);
    }
    else {
      String message = String.format("Cannot initialize field %s.%s: it's not bound to any page object",
        field.getDeclaringClass().getSimpleName(), field.getName());
      throw new IllegalArgumentException(message);
    }
  }

  private List<WebElement> createWebElementsList(ClassLoader loader, Driver driver, @Nullable WebElementSource searchContext,
                                                 Field field) {
    ElementLocatorFactory factory = fieldLocatorFactory(driver, searchContext);
    ElementLocator locator = factory.createLocator(field);
    SelenideFieldDecorator decorator = new SelenideFieldDecorator(factory);
    return decorator.proxyForListLocator(loader, locator);
  }

  @Nonnull
  protected SelenideElement decorateWebElement(Driver driver, @Nullable WebElementSource searchContext, By selector,
                                               Field field, @Nullable String alias) {
    return shouldCache(field) ?
      LazyWebElementSnapshot.wrap(new ElementFinder(driver, searchContext, selector, 0, alias)) :
      ElementFinder.wrap(driver, SelenideElement.class, searchContext, selector, 0, alias);
  }

  @Nonnull
  protected BaseElementsCollection<? extends SelenideElement, ? extends BaseElementsCollection<?, ?>> createElementsCollection(
    Driver driver, @Nullable WebElementSource searchContext,
    By selector, Field field, @Nullable String alias) {
    CollectionSource collection = new BySelectorCollection(driver, searchContext, selector);
    if (alias != null) {
      collection.setAlias(alias);
    }
    if (shouldCache(field)) {
      collection = new LazyCollectionSnapshot(collection);
    }
    return createCollection(collection, field.getType());
  }

  @Nonnull
  protected BaseElementsCollection<? extends SelenideElement, ? extends BaseElementsCollection<?, ?>> createCollection(
    CollectionSource collection, Class<?> klass) {
    return new ElementsList(collection);
  }

  @CheckReturnValue
  @Nonnull
  protected FieldDecorator defaultFieldDecorator(Driver driver, @Nullable WebElementSource searchContext) {
    return new DefaultFieldDecorator(fieldLocatorFactory(driver, searchContext));
  }

  @Nonnull
  @CheckReturnValue
  private DefaultElementLocatorFactory fieldLocatorFactory(Driver driver, @Nullable WebElementSource searchContext) {
    SearchContext context = getSearchContext(driver, searchContext);
    return new DefaultElementLocatorFactory(context);
  }

  @Nonnull
  @CheckReturnValue
  protected SearchContext getSearchContext(Driver driver, @Nullable WebElementSource searchContext) {
    return searchContext == null ? driver.getWebDriver() : searchContext.getWebElement();
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
  protected boolean isDecoratableList(Field field, @Nullable By selector, Type[] genericTypes, Class<?> type) {
    if (!List.class.isAssignableFrom(field.getType())) {
      return false;
    }

    Class<?> listType = getListGenericType(field, genericTypes);
    return listType != null && type.isAssignableFrom(listType) && selector != null;
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

  private static class ElementsList extends ElementsCollection implements NoOpsList<SelenideElement> {

    ElementsList(CollectionSource collection) {
      super(collection);
    }
  }
}
