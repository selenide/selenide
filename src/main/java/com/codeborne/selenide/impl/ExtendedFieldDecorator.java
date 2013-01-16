package com.codeborne.selenide.impl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.lang.reflect.Field;

public class ExtendedFieldDecorator extends DefaultFieldDecorator {
  public ExtendedFieldDecorator(WebDriver webDriver) {
    super(new DefaultElementLocatorFactory(webDriver));
  }

  @Override
  public Object decorate(ClassLoader loader, Field field) {
    return super.decorate(loader, field);    //To change body of overridden methods use File | Settings | File Templates.
  }
}
