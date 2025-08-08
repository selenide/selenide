package com.codeborne.selenide.impl;

import com.codeborne.selenide.Container;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.PageObjectException;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import static com.codeborne.selenide.impl.SelenideAnnotations.isShadowRoot;

public interface PageObjectFactory {
  <PageObjectClass> PageObjectClass page(Driver driver, Class<PageObjectClass> pageObjectClass);

  <PageObjectClass, T extends PageObjectClass> PageObjectClass page(Driver driver, T pageObject);

  Container createElementsContainer(Driver driver, WebElementSource searchContext, Field field, By selector);

  default <ContainerClass extends Container> ContainerClass createElementsContainer(Driver driver,
                                                                                    By selector,
                                                                                    int index,
                                                                                    Class<ContainerClass> containerClass,
                                                                                    Type[] genericTypes) {

    WebElementSource self = new ElementFinder(driver, null, selector, index, isShadowRoot(containerClass), null);
    return initElementsContainer(driver, self, containerClass, genericTypes);
  }

  default <ContainerClass extends Container> ContainerClass createElementsContainer(Driver driver,
                                                                                    WebElement webElement,
                                                                                    Class<ContainerClass> containerClass,
                                                                                    Type[] genericTypes) {
    WebElementSource self = new WebElementWrapper(driver, webElement, null, isShadowRoot(containerClass));
    return initElementsContainer(driver, self, containerClass, genericTypes);
  }

  @SuppressWarnings("unchecked")
  private <ContainerClass extends Container> ContainerClass initElementsContainer(Driver driver,
                                                                                    WebElementSource self,
                                                                                    Class<ContainerClass> containerClass,
                                                                                    Type[] genericTypes) {
    try {
      return (ContainerClass) initElementsContainer(driver, null, self, containerClass, genericTypes);
    }
    catch (ReflectiveOperationException e) {
      throw new PageObjectException("Failed to create elements container of type " + containerClass.getName(), e);
    }
  }

  default <ContainerClass extends Container> List<ContainerClass> createElementsContainerList(Driver driver,
                                                                                              By selector,
                                                                                              Class<ContainerClass> listType,
                                                                                              Type[] genericTypes) {
    CollectionSource collection = new BySelectorCollection(driver, null, selector);
    return new ElementsContainerCollection<>(this, driver, null, listType, genericTypes, collection);
  }

  default <ContainerClass extends Container> List<ContainerClass> createElementsContainerList(Driver driver,
                                                                                              Collection<? extends WebElement> elements,
                                                                                              Class<ContainerClass> listType,
                                                                                              Type[] genericTypes) {
    CollectionSource collection = new WebElementsCollectionWrapper(driver, elements);
    return new ElementsContainerCollection<>(this, driver, null, listType, genericTypes, collection);
  }


  Container initElementsContainer(Driver driver,
                                  @Nullable Field field,
                                  WebElementSource self,
                                  Class<?> type,
                                  Type[] genericTypes) throws ReflectiveOperationException;
}
