package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.DOM;
import com.codeborne.selenide.ShouldableWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.DOM.waitUntil;
import static com.codeborne.selenide.DOM.waitWhile;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.lang.Thread.currentThread;

public class WebElementWaitingProxy implements InvocationHandler {
  public static ShouldableWebElement wrap(WebElement parent, By criteria, int index) {
    return (ShouldableWebElement) Proxy.newProxyInstance(
        currentThread().getContextClassLoader(),
        new Class<?>[]{ShouldableWebElement.class}, new WebElementWaitingProxy(parent, criteria, index));
  }

  private final WebElement parent;
  private final By criteria;
  private final int index;

  private WebElementWaitingProxy(WebElement parent, By criteria, int index) {
    this.parent = parent;
    this.criteria = criteria;
    this.index = index;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if ("setValue".equals(method.getName())) {
      DOM.setValue(waitForElement(), (String) args[0]);
      return null;
    }
    if ("should".equals(method.getName()) || "shouldHave".equals(method.getName()) || "shouldBe".equals(method.getName())) {
      return should(proxy, (Condition[]) args[0]);
    }
    if ("shouldNot".equals(method.getName()) || "shouldNotHave".equals(method.getName()) || "shouldNotBe".equals(method.getName())) {
      return shouldNot(proxy, (Condition[]) args[0]);
    }
    if ("find".equals(method.getName())) {
      return find(args[0], args.length == 1 ? 0 : (Integer) args[1]);
    }
    if ("toString".equals(method.getName())) {
      return describe();
    }
    if ("uploadFromClasspath".equals(method.getName())) {
      return ShouldableWebElementProxy.uploadFromClasspath(waitForElement(), (String) args[0]);
    }

    return ShouldableWebElementProxy.delegateMethod(waitForElement(), method, args);
  }

  private String describe() {
    try {
      if (index == 0) {
        return Describe.describe(getWebDriver().findElement(criteria));
      }
      else {
        return Describe.describe(getWebDriver().findElements(criteria).get(index));
      }
    } catch (WebDriverException e) {
      return "null";
    } catch (IndexOutOfBoundsException e) {
      return "null";
    }
  }

  private Object should(Object proxy, Condition[] conditions) {
    for (Condition condition : conditions) {
      waitUntil(parent, criteria, index, condition);
    }
    return proxy;
  }

  private Object shouldNot(Object proxy, Condition[] conditions) {
    for (Condition condition : conditions) {
      waitWhile(parent, criteria, index, condition);
    }
    return proxy;
  }

  private ShouldableWebElement find(Object arg, int index) {
    return arg instanceof By ?
      wrap(waitForElement(), (By) arg, index) :
      wrap(waitForElement(), By.cssSelector((String) arg), index);
  }

  private ShouldableWebElement waitForElement() {
    return waitUntil(parent, criteria, index, exist);
  }
}
