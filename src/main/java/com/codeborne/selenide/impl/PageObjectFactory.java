package com.codeborne.selenide.impl;

import com.codeborne.selenide.Container;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.By;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

@ParametersAreNonnullByDefault
public interface PageObjectFactory {
  <PageObjectClass> PageObjectClass page(Driver driver, Class<PageObjectClass> pageObjectClass);

  <PageObjectClass, T extends PageObjectClass> PageObjectClass page(Driver driver, T pageObject);

  Container createElementsContainer(Driver driver, WebElementSource searchContext, Field field, By selector);

  Container initElementsContainer(Driver driver,
                                  Field field,
                                  WebElementSource self,
                                  Class<?> type,
                                  Type[] genericTypes) throws ReflectiveOperationException;
}
