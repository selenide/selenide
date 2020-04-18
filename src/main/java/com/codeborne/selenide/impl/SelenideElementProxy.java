package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.Stopwatch;
import com.codeborne.selenide.commands.Commands;
import com.codeborne.selenide.ex.ElementIsNotClickableException;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.logevents.SelenideLog;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.WebDriverException;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.logevents.ErrorsCollector.validateAssertionMode;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static java.util.Arrays.asList;

class SelenideElementProxy implements InvocationHandler {
  private static final Set<String> methodsToSkipLogging = new HashSet<>(asList(
      "toWebElement",
      "getWrappedElement",
      "toString",
      "getSearchCriteria"
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
      return Commands.getInstance().execute(proxy, webElementSource, method.getName(), args);

    validateAssertionMode(config());

    long timeoutMs = getTimeoutMs(method, args);
    long pollingIntervalMs = getPollingIntervalMs(method, args);
    SelenideLog log = SelenideLogger.beginStep(webElementSource.getSearchCriteria(), method.getName(), args);
    try {
      Object result = dispatchAndRetry(timeoutMs, pollingIntervalMs, proxy, method, args);
      SelenideLogger.commitStep(log, PASS);
      return result;
    }
    catch (Error error) {
      Error wrappedError = UIAssertionError.wrap(driver(), error, timeoutMs);
      SelenideLogger.commitStep(log, wrappedError);
      if (config().assertionMode() == SOFT && methodsForSoftAssertion.contains(method.getName()))
        return proxy;
      else
        throw wrappedError;
    }
    catch (RuntimeException error) {
      SelenideLogger.commitStep(log, error);
      throw error;
    }
  }

  private Driver driver() {
    return webElementSource.driver();
  }

  private Config config() {
    return driver().config();
  }

  protected Object dispatchAndRetry(long timeoutMs, long pollingIntervalMs,
                                    Object proxy, Method method, Object[] args) throws Throwable {
    Stopwatch stopwatch = new Stopwatch(timeoutMs);

    Throwable lastError;
    do {
      try {
        if (SelenideElement.class.isAssignableFrom(method.getDeclaringClass())) {
          return Commands.getInstance().execute(proxy, webElementSource, method.getName(), args);
        }

        return method.invoke(webElementSource.getWebElement(), args);
      }
      catch (InvocationTargetException e) {
        lastError = e.getTargetException();
      }
      catch (WebDriverException | IndexOutOfBoundsException | AssertionError e) {
        lastError = e;
      }

      if (Cleanup.of.isInvalidSelectorError(lastError)) {
        throw Cleanup.of.wrap(lastError);
      }
      else if (!shouldRetryAfterError(lastError)) {
        throw lastError;
      }
      stopwatch.sleep(pollingIntervalMs);
    }
    while (!stopwatch.isTimeoutReached());

    if (lastError instanceof UIAssertionError) {
      throw lastError;
    }
    else if (lastError instanceof InvalidElementStateException) {
      throw new InvalidStateException(driver(), lastError);
    }
    else if (isElementNotClickableException(lastError)) {
      throw new ElementIsNotClickableException(driver(), lastError);
    }
    else if (lastError instanceof WebDriverException) {
      throw webElementSource.createElementNotFoundError(exist, lastError);
    }
    throw lastError;
  }

  private boolean isElementNotClickableException(Throwable e) {
    return e instanceof WebDriverException && e.getMessage().contains("is not clickable");
  }

  static boolean shouldRetryAfterError(Throwable e) {
    if (e instanceof FileNotFoundException) return false;
    if (e instanceof IllegalArgumentException) return false;
    if (e instanceof ReflectiveOperationException) return false;
    if (e instanceof JavascriptException) return false;

    return e instanceof Exception || e instanceof AssertionError;
  }

  private long getTimeoutMs(Method method, Object[] args) {
    return isWaitCommand(method) ?
      args.length == 3 ? (Long) args[args.length - 2] : (Long) args[args.length - 1] :
      config().timeout();
  }

  private long getPollingIntervalMs(Method method, Object[] args) {
    return isWaitCommand(method) && args.length == 3 ? (Long) args[args.length - 1] : config().pollingInterval();
  }

  private boolean isWaitCommand(Method method) {
    return "waitUntil".equals(method.getName()) || "waitWhile".equals(method.getName());
  }
}
