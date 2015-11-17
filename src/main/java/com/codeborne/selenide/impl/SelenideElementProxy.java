package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.commands.Commands;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Configuration.AssertionMode.SOFT;
import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASSED;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;

class SelenideElementProxy implements InvocationHandler {
  private static final Set<String> methodsToSkipLogging = new HashSet<>(asList(
      "toWebElement",
      "toString"
  ));

  private static final Set<String> methodsForSoftAssertion = new HashSet<>(asList(
      "should",
      "shouldBe",
      "shouldHave",
      "shouldNot",
      "shouldNotHave",
      "shouldNotBe",
      "waitUntil",
      "waitWhile"
  ));

  private final WebElementSource webElementSource;
  
  protected SelenideElementProxy(WebElementSource webElementSource) {
    this.webElementSource = webElementSource;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
    if (methodsToSkipLogging.contains(method.getName()))
      return dispatchSelenideMethod(proxy, method, args);

    SelenideLog log = SelenideLogger.beginStep(webElementSource.getSearchCriteria(), method.getName(), args);
    try {
      Object result = dispatchAndRetry(proxy, method, args);
      SelenideLogger.commitStep(log, PASSED);
      return result;
    }
    catch (Error error) {
      SelenideLogger.commitStep(log, error);
      if (assertionMode == SOFT && methodsForSoftAssertion.contains(method.getName()))
        return proxy;
      else
        throw UIAssertionError.wrap(error);
    }
    catch (RuntimeException error) {
      SelenideLogger.commitStep(log, error);
      throw error;
    }
  }

  protected Object dispatchAndRetry(Object proxy, Method method, Object[] args) throws Throwable {
    long timeoutMs = getTimeoutMs(method, args);

    final long startTime = currentTimeMillis();
    Throwable lastError;
    do {
      try {
        return method.getDeclaringClass() == SelenideElement.class ?
            dispatchSelenideMethod(proxy, method, args) :
            delegateSeleniumMethod(webElementSource.getWebElement(), method, args);
      }
      catch (Throwable e) {
        if (Cleanup.of.isInvalidSelectorError(e)) {
          throw Cleanup.of.wrap(e);
        }
        lastError = e;
        sleep(pollingInterval);
      }
    }
    while (currentTimeMillis() - startTime <= timeoutMs);

    if (lastError instanceof UIAssertionError) {
      UIAssertionError uiError = (UIAssertionError) lastError;
      uiError.timeoutMs = timeoutMs;
      throw uiError;
      
    }
    else if (lastError instanceof InvalidElementStateException) {
      InvalidStateException uiError = new InvalidStateException(lastError);
      uiError.timeoutMs = timeoutMs;
      throw uiError;
    }
    else if (lastError instanceof WebDriverException) {
      ElementNotFound uiError = webElementSource.createElementNotFoundError(exist, lastError);
      uiError.timeoutMs = timeoutMs;
      throw uiError;
    }
    throw lastError;
  }

  private long getTimeoutMs(Method method, Object[] args) {
    return "waitUntil".equals(method.getName()) || "waitWhile".equals(method.getName()) ?
        (Long) args[args.length - 1] : timeout;
  }

  protected Object dispatchSelenideMethod(Object proxy, Method method, Object[] args) throws Throwable {
    return Commands.collection.execute(proxy, webElementSource, method.getName(), args);
  }
  
  static Object delegateSeleniumMethod(WebElement delegate, Method method, Object[] args) throws Throwable {
    try {
      return method.invoke(delegate, args);
    } catch (InvocationTargetException e) {
      throw e.getTargetException();
    }
  }
}
