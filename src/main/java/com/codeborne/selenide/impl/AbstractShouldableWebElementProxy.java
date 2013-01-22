package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.DOM;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;

import static com.codeborne.selenide.Condition.present;
import static com.codeborne.selenide.Navigation.sleep;
import static com.codeborne.selenide.WebDriverRunner.fail;
import static java.lang.Thread.currentThread;

abstract class AbstractShouldableWebElementProxy implements InvocationHandler {
  abstract WebElement getDelegate();
  abstract WebElement getActualDelegate() throws NoSuchElementException, IndexOutOfBoundsException;

//  abstract Object should(Object proxy, Condition[] conditions);
//  abstract Object shouldNot(Object proxy, Condition[] conditions);
//  abstract WebElement find(Object arg, int index);

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if ("type".equals(method.getName()) || "setValue".equals(method.getName())) {
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
      return ShouldableWebElementProxy.wrap(args.length == 1 ? find(args[0], 0) : find(args[0], (Integer) args[1]));
    }
    if ("toString".equals(method.getName())) {
      return describe();
    }
    if ("exists".equals(method.getName())) {
      return exists();
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

  private Object should(Object proxy, Condition[] conditions) {
    for (Condition condition : conditions) {
      waitUntil(condition, DOM.defaultWaitingTimeout);
    }
    return proxy;
  }

  private Object shouldNot(Object proxy, Condition[] conditions) {
    for (Condition condition : conditions) {
      waitWhile(condition, DOM.defaultWaitingTimeout);
    }
    return proxy;
  }



  private Object uploadFromClasspath(WebElement inputField, String fileName) throws URISyntaxException {
    if (!"input".equalsIgnoreCase(inputField.getTagName())) {
      throw new IllegalArgumentException("Cannot upload file because " + Describe.describe(inputField) + " is not an INPUT");
    }

    URL resource = currentThread().getContextClassLoader().getResource(fileName);
    if (resource == null) {
      throw new IllegalArgumentException("File not found in classpath: " + fileName);
    }
    File file = new File(resource.toURI());
    inputField.sendKeys(file.getAbsolutePath());
    return file;
  }

  private void selectOptionByText(WebElement selectField, String optionText) {
    // TODO wait until the element has option with given text
    DOM.waitUntil(selectField, By.tagName("option"), 0, present);
    new Select(selectField).selectByVisibleText(optionText);
  }

  private void selectOptionByValue(WebElement selectField, String optionValue) {
    // TODO wait until the element has option with given value
    DOM.waitUntil(selectField, By.tagName("option"), 0, present);
    new Select(selectField).selectByValue(optionValue);
  }

  private boolean exists() {
    try {
      return getActualDelegate() != null;
    } catch (WebDriverException elementDoesNotExist) {
      return false;
    } catch (IndexOutOfBoundsException invalidElementIndex) {
      return false;
    }
  }

  private String describe() {
    try {
      return Describe.describe(getActualDelegate());
    } catch (WebDriverException elementDoesNotExist) {
      return elementDoesNotExist.toString();
    } catch (IndexOutOfBoundsException invalidElementIndex) {
      return invalidElementIndex.toString();
    }
  }

  private Object delegateMethod(WebElement delegate, Method method, Object[] args) throws Throwable {
    try {
      return method.invoke(delegate, args);
    } catch (InvocationTargetException e) {
      throw e.getTargetException();
    }
  }

  protected WebElement waitUntil(Condition condition, long timeoutMs) {
    final long startTime = System.currentTimeMillis();
    WebElement element;
    do {
      element = tryToGetElement();
      if (element != null) {
        try {
          if (condition.apply(element)) {
            return element;
          }
        }
        catch (WebDriverException ignore) {
        }
        catch (IndexOutOfBoundsException ignore) {
        }
      }
      else if (condition.applyNull()) {
        return null;
      }
      sleep(100);
    }
    while (System.currentTimeMillis() - startTime < timeoutMs);

    return fail("Element " + toString() + " hasn't " + condition + " in " + timeoutMs + " ms.;" +
        " actual value: '" + getActualValue(condition) + "';" +
        (element == null ? "" : " element details: '" + Describe.describe(element) + "'"));
  }

  protected void waitWhile(Condition condition, long timeoutMs) {
    final long startTime = System.currentTimeMillis();
    WebElement element;
    do {
      element = tryToGetElement();
      if (element != null) {
        try {
          if (!condition.apply(element)) {
            return;
          }
        }
        catch (WebDriverException ignore) {
        }
        catch (IndexOutOfBoundsException ignore) {
        }
      }
      else if (!condition.applyNull()) {
        return;
      }
      sleep(100);
    }
    while (System.currentTimeMillis() - startTime < timeoutMs);

    fail("Element " + toString() + " has " + condition + " in " + timeoutMs + " ms.;" +
        " actual value: '" + getActualValue(condition) + "';" +
        (element == null ? "" : " element details: '" + Describe.describe(element) + "'"));
  }

  private WebElement tryToGetElement() {
    try {
      return getActualDelegate();
    } catch (WebDriverException ignore) {
      return null;
    } catch (IndexOutOfBoundsException ignore) {
      return null;
    }
  }

  private String getActualValue(Condition condition) {
    try {
      WebElement element = getActualDelegate();
      return condition.actualValue(element);
    }
    catch (WebDriverException e) {
      return e.toString();
    }
    catch (IndexOutOfBoundsException e) {
      return e.toString();
    }
  }

  protected WebElement find(Object arg, int index) {
    if (index == 0) {
      return arg instanceof By ?
          getDelegate().findElement((By) arg) :
          getDelegate().findElement(By.cssSelector((String) arg));
    } else {
      return arg instanceof By ?
          getDelegate().findElements((By) arg).get(index) :
          getDelegate().findElements(By.cssSelector((String) arg)).get(index);
    }
  }
}
