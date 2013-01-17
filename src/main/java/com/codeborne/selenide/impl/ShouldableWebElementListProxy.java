package com.codeborne.selenide.impl;

import com.codeborne.selenide.ShouldableWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class ShouldableWebElementListProxy implements InvocationHandler {

  private ElementLocator locator;

  @SuppressWarnings("unchecked")
  public static List<ShouldableWebElement> wrap(ElementLocator locator) {
    InvocationHandler handler = new ShouldableWebElementListProxy(locator);

    return (List<ShouldableWebElement>) Proxy.newProxyInstance(
        ShouldableWebElementListProxy.class.getClassLoader(), new Class[]{List.class}, handler);
  }

  private ShouldableWebElementListProxy(ElementLocator locator) {
    this.locator = locator;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    List<ShouldableWebElement> elements = new ArrayList<ShouldableWebElement>();
    for (WebElement webElement : locator.findElements()) {
      elements.add(ShouldableWebElementProxy.wrap(webElement));
    }
    try {
      return method.invoke(elements, args);
    } catch (InvocationTargetException e) {
      // Unwrap the underlying exception
      throw e.getCause();
    }
  }
}
