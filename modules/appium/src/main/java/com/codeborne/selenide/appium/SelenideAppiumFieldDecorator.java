package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.BySelectorCollection;
import com.codeborne.selenide.impl.ElementFinder;
import com.codeborne.selenide.impl.SelenideElementListProxy;
import com.codeborne.selenide.impl.SelenideFieldDecorator;
import com.codeborne.selenide.impl.SelenidePageFactory;
import io.appium.java_client.HasSessionDetails;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.DefaultElementByBuilder;
import io.appium.java_client.pagefactory.bys.builder.AppiumByBuilder;
import org.openqa.selenium.By;
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
  private final Driver driver;
  private final ElementLocatorFactory factory;
  private final AppiumByBuilder builder;

  public SelenideAppiumFieldDecorator(Driver driver) {
    super(driver.getWebDriver());
    this.driver = driver;
    this.factory = new DefaultElementLocatorFactory(driver.getWebDriver());
    this.builder = byBuilder(driver);
  }

  private DefaultElementByBuilder byBuilder(Driver driver) {
    if (driver == null
      || !HasSessionDetails.class.isAssignableFrom(driver.getWebDriver().getClass())) {
      return new DefaultElementByBuilder(null, null);
    }
    else {
      HasSessionDetails d = (HasSessionDetails) driver.getWebDriver();
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
    }
    else if (SelenideElement.class.isAssignableFrom(field.getType())) {
      return ElementFinder.wrap(driver, driver.getWebDriver(), selector, 0);
    }
    else if (ElementsCollection.class.isAssignableFrom(field.getType())) {
      return new ElementsCollection(new BySelectorCollection(driver, selector));
    }
    else if (ElementsContainer.class.isAssignableFrom(field.getType())) {
      return createElementsContainer(selector, field);
    }
    else if (isDecoratableList(field, ElementsContainer.class)) {
      return createElementsContainerList(field);
    }
    else if (isDecoratableList(field, SelenideElement.class)) {
      return SelenideElementListProxy.wrap(driver, factory.createLocator(field));
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
      SelenideElement self = ElementFinder.wrap(driver, driver.getWebDriver(), selector, 0);
      return initElementsContainer(field.getType(), self);
    }
    catch (Exception e) {
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
    List<SelenideElement> selfList = SelenideElementListProxy.wrap(driver, factory.createLocator(field));

    return selfList
      .stream()
      .map(element -> initElementsContainerList(field, listType, element))
      .collect(toList());
  }

  private ElementsContainer initElementsContainerList(Field field, Class<?> listType, SelenideElement element) {
    try {
      return initElementsContainer(listType, element);
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to create elements container list for field " + field.getName(), e);
    }
  }

  private ElementsContainer initElementsContainer(Class<?> type, SelenideElement self) throws Exception {
    Constructor<?> constructor = type.getDeclaredConstructor();
    constructor.setAccessible(true);
    ElementsContainer result = (ElementsContainer) constructor.newInstance();
    PageFactory.initElements(new SelenideFieldDecorator(new SelenidePageFactory(), driver, self), result);
    result.setSelf(self);
    return result;
  }

  private Class<?> getListGenericType(Field field) {
    Type genericType = field.getGenericType();
    if (!(genericType instanceof ParameterizedType)) return null;

    return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
  }
}
