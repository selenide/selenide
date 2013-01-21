package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.DOM;
import com.codeborne.selenide.ShouldableWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.ui.Select;

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

  public static ShouldableWebElement wrap(ElementLocator elementLocator) {
    return (ShouldableWebElement) Proxy.newProxyInstance(
        elementLocator.getClass().getClassLoader(), new Class<?>[]{ShouldableWebElement.class}, new ShouldableWebElementProxy(elementLocator));
  }

  private WebElement delegate;
  private ElementLocator elementLocator;

  private ShouldableWebElementProxy(WebElement delegate) {
    this.delegate = delegate;
  }

  private ShouldableWebElementProxy(ElementLocator elementLocator) {
    this.elementLocator = elementLocator;
  }

  private WebElement getDelegate() {
    if (delegate != null) return delegate;
    else if (elementLocator != null) return locateElement();
    else return null;
  }

  private WebElement locateElement() {
    long startTime = System.currentTimeMillis();
    NoSuchElementException exception;
    do {
      try {
        return elementLocator.findElement();
      } catch (NoSuchElementException e) {
        exception = e;
        try {
          Thread.sleep(50);
        } catch (InterruptedException ignore) {}
      }
    } while (System.currentTimeMillis() - startTime < DOM.defaultWaitingTimeout);
    throw exception;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if ("setValue".equals(method.getName())) {
      DOM.setValue(getDelegate(), (String) args[0]);
      return null;
    }
    if ("text".equals(method.getName())) {
      return getDelegate().getText();
    }
    if ("val".equals(method.getName())) {
      return getDelegate().getAttribute("value");
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
      return describe(getDelegate());
    }
    if ("exists".equals(method.getName())) {
      return exists(getDelegate());
    }
    if ("uploadFromClasspath".equals(method.getName())) {
      return uploadFromClasspath(getDelegate(), (String) args[0]);
    }
    if ("selectOption".equals(method.getName())) {
      selectOptionByText(getDelegate(), (String) args[0]);
      return null;
    }
    if ("selectOptionByValue".equals(method.getName())) {
      selectOptionByValue(getDelegate(), (String) args[0]);
      return null;
    }

    return delegateMethod(getDelegate(), method, args);
  }

  private boolean exists(WebElement delegate) {
    return getDelegate() != null;
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

  static void selectOptionByText(WebElement selectField, String optionText) {
    new Select(selectField).selectByVisibleText(optionText);
  }

  static void selectOptionByValue(WebElement selectField, String optionValue) {
    new Select(selectField).selectByValue(optionValue);
  }

  private Object should(Object proxy, Condition[] conditions) {
    for (Condition condition : conditions) {
      assertElement(getDelegate(), condition);
    }
    return proxy;
  }

  private Object shouldNot(Object proxy, Condition[] conditions) {
    for (Condition condition : conditions) {
      if (condition.apply(getDelegate())) {
        fail("Element " + getDelegate().getTagName() + " has " + condition);
      }
    }
    return proxy;
  }

  private WebElement find(Object arg) {
    return arg instanceof By ?
        getDelegate().findElement((By) arg) :
        getDelegate().findElement(By.cssSelector((String) arg));
  }

  private WebElement find(Object arg, int index) {
    return arg instanceof By ?
        getDelegate().findElements((By) arg).get(index) :
        getDelegate().findElements(By.cssSelector((String) arg)).get(index);
  }

  static Object delegateMethod(WebElement delegate, Method method, Object[] args) throws Throwable {
    try {
      return method.invoke(delegate, args);
    } catch (InvocationTargetException e) {
      throw e.getTargetException();
    }
  }
}