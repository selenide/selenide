package com.codeborne.selenide.appium;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.BySelectorCollection;
import com.codeborne.selenide.impl.ElementFinder;
import com.codeborne.selenide.impl.SelenideElementListProxy;
import com.codeborne.selenide.impl.SelenideFieldDecorator;
import io.appium.java_client.HasSessionDetails;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.DefaultElementByBuilder;
import io.appium.java_client.pagefactory.bys.builder.AppiumByBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.ByIdOrName;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.Annotations;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class SelenideAppiumFieldDecorator extends AppiumFieldDecorator {
  private final SearchContext searchContext;
  private final ElementLocatorFactory factory;
  private final AppiumByBuilder builder;

  public SelenideAppiumFieldDecorator(SearchContext context) {
    super(context);
    this.searchContext = context;
    this.factory = new DefaultElementLocatorFactory(searchContext);
    this.builder = byBuilder(context);
  }

  private DefaultElementByBuilder byBuilder(SearchContext driver) {
    if (driver == null
      || !HasSessionDetails.class.isAssignableFrom(driver.getClass())) {
      return new DefaultElementByBuilder(null, null);
    } else {
      HasSessionDetails d = HasSessionDetails.class.cast(driver);
      return new DefaultElementByBuilder(d.getPlatformName(), d.getAutomationName());
    }
  }

  @Override
  public Object decorate(ClassLoader loader, Field field) {
    builder.setAnnotated(field);
    By selector = builder.buildBy();

    if (selector == null) {
      selector = new Annotations(field).buildBy();
    }

    if (selector instanceof ByIdOrName) {
      return decorateWithAppium(loader, field);
    } else if (SelenideElement.class.isAssignableFrom(field.getType())) {
      return ElementFinder.wrap(searchContext, selector, 0);
    } else if (ElementsCollection.class.isAssignableFrom(field.getType())) {
      return new ElementsCollection(new BySelectorCollection(searchContext, selector));
    } else if (ElementsContainer.class.isAssignableFrom(field.getType())) {
      return createElementsContainer(selector, field);
    } else if (isDecoratableList(field, ElementsContainer.class)) {
      return createElementsContainerList(field);
    } else if (isDecoratableList(field, SelenideElement.class)) {
      return SelenideElementListProxy.wrap(factory.createLocator(field));
    }

    return super.decorate(loader, field);
  }

  private Object decorateWithAppium(ClassLoader loader, Field field) {
    Object appiumElement = super.decorate(loader, field);
    if (appiumElement instanceof MobileElement) {
      return Selenide.$((MobileElement) appiumElement);
    }
    return appiumElement;
  }

  private ElementsContainer createElementsContainer(By selector, Field field) {
    try {
      SelenideElement self = ElementFinder.wrap(searchContext, selector, 0);
      return initElementsContainer(field.getType(), self);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create elements container for field " + field.getName(), e);
    }
  }

  private boolean isDecoratableList(Field field, Class<?> type) {
    if (!List.class.isAssignableFrom(field.getType())) {
      return false;
    }

    Class<?> listType = getListGenericType(field);

    return listType != null && type.isAssignableFrom(listType)
      && (field.getAnnotation(FindBy.class) != null || field.getAnnotation(FindBys.class) != null);
  }

  private List<ElementsContainer> createElementsContainerList(Field field) {
    Class<?> listType = getListGenericType(field);
    List<SelenideElement> selfList = SelenideElementListProxy.wrap(factory.createLocator(field));

    return selfList
      .stream()
      .map(element -> initElementsContainerList(field, listType, element))
      .collect(toList());
  }

  private ElementsContainer initElementsContainerList(Field field, Class<?> listType, SelenideElement element) {
    try {
      return initElementsContainer(listType, element);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create elements container list for field " + field.getName(), e);
    }
  }

  private ElementsContainer initElementsContainer(Class<?> type, SelenideElement self) throws Exception {
    Constructor<?> constructor = type.getDeclaredConstructor();
    constructor.setAccessible(true);
    ElementsContainer result = (ElementsContainer) constructor.newInstance();
    PageFactory.initElements(new SelenideFieldDecorator(self), result);
    result.setSelf(self);
    return result;
  }

  private Class<?> getListGenericType(Field field) {
    Type genericType = field.getGenericType();
    if (!(genericType instanceof ParameterizedType)) return null;

    return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
  }
}
