package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

@ParametersAreNonnullByDefault
public interface PageObjectFactory {
  <PageObjectClass> PageObjectClass page(Driver driver, Class<PageObjectClass> pageObjectClass);

  <PageObjectClass, T extends PageObjectClass> PageObjectClass page(Driver driver, T pageObject);

  ElementsContainer createElementsContainer(Driver driver, SearchContext searchContext, Field field, By selector);

  ElementsContainer initElementsContainer(Driver driver,
                                          Field field,
                                          SelenideElement self,
                                          Class<?> type,
                                          Type[] genericTypes) throws ReflectiveOperationException;
}
