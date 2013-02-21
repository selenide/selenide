package com.codeborne.selenide.impl;

import com.codeborne.selenide.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;

import static com.codeborne.selenide.Condition.present;
import static com.codeborne.selenide.Navigation.sleep;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.defaultWaitingTimeout;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.cleanupWebDriverExceptionMessage;
import static com.codeborne.selenide.WebDriverRunner.fail;
import static com.codeborne.selenide.impl.ShouldableWebElementProxy.wrap;
import static java.lang.Thread.currentThread;
import static org.openqa.selenium.Keys.TAB;

abstract class AbstractShouldableWebElementProxy implements InvocationHandler {
  protected JQuery jQuery = new JQuery();

  abstract WebElement getDelegate();
  abstract WebElement getActualDelegate() throws NoSuchElementException, IndexOutOfBoundsException;

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if ("setValue".equals(method.getName())) {
      setValue((String) args[0]);
      return proxy;
    }
    else if ("val".equals(method.getName())) {
      if (args == null || args.length == 0) {
        return getDelegate().getAttribute("value");
      }
      else {
        setValue((String) args[0]);
        return proxy;
      }
    }
    else if ("append".equals(method.getName())) {
      append((String) args[0]);
      return proxy;
    }
    else if ("pressEnter".equals(method.getName())) {
      getDelegate().sendKeys(Keys.ENTER);
      return proxy;
    }
    else if ("followLink".equals(method.getName())) {
      followLink(getDelegate());
      return null;
    }
    else if ("text".equals(method.getName())) {
      return getDelegate().getText();
    }
    else if ("should".equals(method.getName()) || "shouldHave".equals(method.getName()) || "shouldBe".equals(method.getName())) {
      return should(proxy, (Condition[]) args[0]);
    }
    else if ("shouldNot".equals(method.getName()) || "shouldNotHave".equals(method.getName()) || "shouldNotBe".equals(method.getName())) {
      return shouldNot(proxy, (Condition[]) args[0]);
    }
    else if ("find".equals(method.getName())) {
      return ShouldableWebElementProxy.wrap(args.length == 1 ? find(args[0], 0) : find(args[0], (Integer) args[1]));
    }
    else if ("toString".equals(method.getName())) {
      return describe();
    }
    else if ("exists".equals(method.getName())) {
      return exists();
    }
    else if ("isDisplayed".equals(method.getName())) {
      return isDisplayed();
    }
    else if ("uploadFromClasspath".equals(method.getName())) {
      return uploadFromClasspath(getDelegate(), (String) args[0]);
    }
    else if ("selectOption".equals(method.getName())) {
      selectOptionByText(getDelegate(), (String) args[0]);
      return null;
    }
    else if ("selectOptionByValue".equals(method.getName())) {
      selectOptionByValue(getDelegate(), (String) args[0]);
      return null;
    }
    else if ("getSelectedOption".equals(method.getName())) {
      return getSelectedOption(getDelegate());
    }
    else if ("getSelectedValue".equals(method.getName())) {
      return getSelectedValue(getDelegate());
    }
    else if ("getSelectedText".equals(method.getName())) {
      return getSelectedText(getDelegate());
    }
    else if ("toWebElement".equals(method.getName())) {
      return getActualDelegate();
    }
    else if ("waitUntil".equals(method.getName())) {
      waitUntil((Condition) args[0], (Long) args[1]);
      return proxy;
    }
    else if ("waitWhile".equals(method.getName())) {
      waitWhile((Condition) args[0], (Long) args[1]);
      return proxy;
    }

    return delegateMethod(getDelegate(), method, args);
  }

  private void followLink(WebElement link) {
    String href = link.getAttribute("href");
    link.click();

    // JavaScript $.click() doesn't take effect for <a href>
    if (href != null) {
      open(href);
    }
  }

  protected void setValue(String text) {
    WebElement element = getDelegate();
    element.clear();
    element.sendKeys(text);
    element.sendKeys(TAB);
  }

  protected void append(String text) {
    WebElement element = getDelegate();
    element.sendKeys(text);
    element.sendKeys(TAB);
  }

  private Object should(Object proxy, Condition[] conditions) {
    for (Condition condition : conditions) {
      waitUntil(condition, defaultWaitingTimeout);
    }
    return proxy;
  }

  private Object shouldNot(Object proxy, Condition[] conditions) {
    for (Condition condition : conditions) {
      waitWhile(condition, defaultWaitingTimeout);
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
    $(selectField).shouldBe(present);
    $(selectField, "option").shouldBe(present);
    // TODO wait until the element has option with given text
    new Select(selectField).selectByVisibleText(optionText);
  }

  private void selectOptionByValue(WebElement selectField, String optionValue) {
    $(selectField).shouldBe(present);
    $(selectField, "option").shouldBe(present);
    // TODO wait until the element has option with given value
    new Select(selectField).selectByValue(optionValue);
  }

  private String getSelectedValue(WebElement selectElement) {
    WebElement option = getSelectedOption(selectElement);
    return option == null ? null : option.getAttribute("value");
  }

  private String getSelectedText(WebElement selectElement) {
    WebElement option = getSelectedOption(selectElement);
    return option == null ? null : option.getText();
  }

  private SelenideElement getSelectedOption(WebElement selectElement) {
    return wrap(new Select(selectElement).getFirstSelectedOption());
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

  private boolean isDisplayed() {
    try {
      WebElement delegate = getActualDelegate();
      return delegate != null && delegate.isDisplayed();
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
      return cleanupWebDriverExceptionMessage(elementDoesNotExist);
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
      return cleanupWebDriverExceptionMessage(e);
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
