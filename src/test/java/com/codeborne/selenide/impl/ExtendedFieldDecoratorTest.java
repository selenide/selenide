package com.codeborne.selenide.impl;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;

import static org.mockito.Mockito.mock;

public class ExtendedFieldDecoratorTest {

  @Test
  public void usesDefaultElementLocatorFactory() throws Exception {
    ExtendedFieldDecorator fieldDecorator = new ExtendedFieldDecorator(mock(WebDriver.class));
    fieldDecorator.getClass().getSuperclass().getDeclaredField("factory").getType().equals(DefaultElementLocatorFactory.class);
  }

  @Test
  public void decoratesShouldableWebElementsAndItsLists() throws Exception {


  }
}
