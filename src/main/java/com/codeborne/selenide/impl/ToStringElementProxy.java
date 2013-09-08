package com.codeborne.selenide.impl;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static com.codeborne.selenide.impl.AbstractSelenideElement.delegateMethod;
import static com.codeborne.selenide.impl.AbstractSelenideElement.isDisplayed;

public class ToStringElementProxy implements InvocationHandler {
  public static WebElement wrap(By selector, WebElement element) {
    return (WebElement) Proxy.newProxyInstance(
        element.getClass().getClassLoader(),
        new Class<?>[]{WebElement.class}, new ToStringElementProxy(selector, element));
  }

  private final By selector;
  private final WebElement element;

  private ToStringElementProxy(By selector, WebElement element) {
    this.selector = selector;
    this.element = element;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if ("toString".equals(method.getName())) {
      return selector + ": " + (isDisplayed(element) ? Describe.describe(element) : "ElementNotFound");
    }

    return delegateMethod(element, method, args);
  }
}
