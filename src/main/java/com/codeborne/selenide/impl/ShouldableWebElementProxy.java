package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.DOM;
import com.codeborne.selenide.ShouldableWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URISyntaxException;
import java.net.URL;

import static com.codeborne.selenide.DOM.assertElement;
import static com.codeborne.selenide.WebDriverRunner.fail;
import static com.codeborne.selenide.impl.Describe.describe;
import static java.lang.Thread.currentThread;

public class ShouldableWebElementProxy implements InvocationHandler {
  public static ShouldableWebElement wrap(WebElement element) {
    return element instanceof ShouldableWebElement ?
        (ShouldableWebElement) element :
        (ShouldableWebElement) Proxy.newProxyInstance(
            element.getClass().getClassLoader(), new Class<?>[]{ShouldableWebElement.class}, new ShouldableWebElementProxy(element));
  }

  private final WebElement delegate;

  private ShouldableWebElementProxy(WebElement delegate) {
    this.delegate = delegate;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if ("setValue".equals(method.getName())) {
      DOM.setValue(delegate, (String) args[0]);
      return null;
    }
    if ("should".equals(method.getName()) || "shouldHave".equals(method.getName()) || "shouldBe".equals(method.getName())) {
      return should(proxy, (Condition[]) args[0]);
    }
    if ("shouldNot".equals(method.getName()) || "shouldNotHave".equals(method.getName()) || "shouldNotBe".equals(method.getName())) {
      return shouldNot(proxy, (Condition[]) args[0]);
    }
    if ("find".equals(method.getName())) {
      return wrap(args.length == 1 ? find(args[0]) : find(args[0], (Integer) args[1]));
    }
    if ("toString".equals(method.getName())) {
      return describe(delegate);
    }
    if ("exists".equals(method.getName())) {
      return exists(delegate);
    }
    if ("uploadFromClasspath".equals(method.getName())) {
      return uploadFromClasspath(delegate, (String) args[0]);
    }

    return delegateMethod(delegate, method, args);
  }

  private boolean exists(WebElement delegate) {
    return delegate != null;
  }

  static Object uploadFromClasspath(WebElement inputField, String fileName) throws URISyntaxException {
    if (!"input".equalsIgnoreCase(inputField.getTagName())) {
      throw new IllegalArgumentException("Cannot upload file because " + describe(inputField) + " is not an INPUT");
    }

    URL resource = currentThread().getContextClassLoader().getResource(fileName);
    if (resource == null) {
      throw new IllegalArgumentException("File not found in classpath: " + fileName);
    }
    File file = new File(resource.toURI());
    inputField.sendKeys(file.getAbsolutePath());
    return file;
  }

  private Object should(Object proxy, Condition[] conditions) {
    for (Condition condition : conditions) {
      assertElement(delegate, condition);
    }
    return proxy;
  }

  private Object shouldNot(Object proxy, Condition[] conditions) {
    for (Condition condition : conditions) {
      if (condition.apply(delegate)) {
        fail("Element " + delegate.getTagName() + " has " + condition);
      }
    }
    return proxy;
  }

  private WebElement find(Object arg) {
    return arg instanceof By ?
        delegate.findElement((By) arg) :
        delegate.findElement(By.cssSelector((String) arg));
  }

  private WebElement find(Object arg, int index) {
    return arg instanceof By ?
        delegate.findElements((By) arg).get(index) :
        delegate.findElements(By.cssSelector((String) arg)).get(index);
  }

  static Object delegateMethod(WebElement delegate, Method method, Object[] args) throws Throwable {
    try {
      return method.invoke(delegate, args);
    } catch (InvocationTargetException e) {
      throw e.getTargetException();
    }
  }
}