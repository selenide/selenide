package com.codeborne.selenide.impl;

import com.codeborne.selenide.Context;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class SelenideElementListProxy implements InvocationHandler {

  @SuppressWarnings("unchecked")
  public static List<SelenideElement> wrap(Context context, ElementLocator locator) {
    InvocationHandler handler = new SelenideElementListProxy(context, locator);

    return (List<SelenideElement>) Proxy.newProxyInstance(
        SelenideElementListProxy.class.getClassLoader(), new Class[]{List.class}, handler);
  }

  private final Context context;
  private final ElementLocator locator;

  private SelenideElementListProxy(Context context, ElementLocator locator) {
    this.context = context;
    this.locator = locator;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    List<SelenideElement> elements = new ArrayList<>();
    for (WebElement webElement : locator.findElements()) {
      elements.add(WebElementWrapper.wrap(context, webElement));
    }
    try {
      return method.invoke(elements, args);
    } catch (InvocationTargetException e) {
      throw e.getCause();
    }
  }
}
