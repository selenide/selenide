package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.Stopwatch;
import com.codeborne.selenide.commands.Commands;
import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.logevents.SelenideLog;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriverException;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static com.codeborne.selenide.logevents.ErrorsCollector.validateAssertionMode;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static java.util.Arrays.asList;

class SelenideElementProxy<T extends SelenideElement> implements InvocationHandler {
  private static final Set<String> methodsToSkipLogging = new HashSet<>(asList(
    "as",
    "getAlias",
    "toWebElement",
    "toString",
    "getSearchCriteria",
    "$",
    "$x",
    "find",
    "$$",
    "$$x",
    "findAll",
    "parent",
    "sibling",
    "preceding",
    "lastChild",
    "closest",
    "ancestor",
    "getSelectedOption"
  ));

  private static final Set<String> methodsForSoftAssertion = new HashSet<>(asList(
    "should",
    "shouldBe",
    "shouldHave",
    "shouldNot",
    "shouldNotHave",
    "shouldNotBe"
  ));

  private final WebElementSource webElementSource;
  private final ExceptionWrapper exceptionWrapper = new ExceptionWrapper();

  protected SelenideElementProxy(WebElementSource webElementSource) {
    this.webElementSource = webElementSource;
  }

  @Nullable
  @Override
  public Object invoke(Object proxy, Method method, Object @Nullable [] args) throws Throwable {
    Arguments arguments = new Arguments(args);
    if (methodsToSkipLogging.contains(method.getName()))
      return Commands.getInstance().execute(proxy, webElementSource, method.getName(), args);

    if (isMethodForSoftAssertion(method)) {
      validateAssertionMode(config());
    }

    long timeoutMs = arguments.getTimeoutMs(config().timeout());
    SelenideLog log = SelenideLogger.beginStep(webElementSource.description(), method.getName(), args);
    try {
      Object result = dispatchAndRetry(timeoutMs, config().pollingInterval(), proxy, method, args);
      SelenideLogger.commitStep(log, PASS);
      return result;
    }
    catch (AssertionError error) {
      Throwable wrappedError = UIAssertionError.wrap(driver(), error, timeoutMs);
      SelenideLogger.commitStep(log, wrappedError);
      return continueOrBreak(proxy, method, wrappedError);
    }
    catch (WebDriverException error) {
      Throwable wrappedError = UIAssertionError.wrap(driver(), error, timeoutMs);
      SelenideLogger.commitStep(log, wrappedError);
      return continueOrBreak(proxy, method, wrappedError);
    }
    catch (RuntimeException | IOException error) {
      SelenideLogger.commitStep(log, error);
      throw error;
    }
  }

  private Object continueOrBreak(Object proxy, Method method, Throwable wrappedError) throws Throwable {
    if (config().assertionMode() == SOFT && isMethodForSoftAssertion(method))
      return proxy;
    else
      throw wrappedError;
  }

  private boolean isMethodForSoftAssertion(Method method) {
    return methodsForSoftAssertion.contains(method.getName());
  }

  private Driver driver() {
    return webElementSource.driver();
  }

  private Config config() {
    return driver().config();
  }

  @Nullable
  protected Object dispatchAndRetry(long timeoutMs, long pollingIntervalMs,
                                    Object proxy, Method method, Object @Nullable [] args) throws Throwable {
    Stopwatch stopwatch = new Stopwatch(timeoutMs);

    Throwable lastError;
    do {
      try {
        if (isSelenideElementMethod(method)) {
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
        throw Cleanup.of.wrapInvalidSelectorException(lastError);
      }
      else if (!shouldRetryAfterError(lastError)) {
        throw lastError;
      }
      stopwatch.sleep(pollingIntervalMs);
    }
    while (!stopwatch.isTimeoutReached());

    throw exceptionWrapper.wrap(lastError, webElementSource);
  }

  static boolean isSelenideElementMethod(Method method) {
    return SelenideElement.class.isAssignableFrom(method.getDeclaringClass());
  }

  private static final Set<Class<? extends Throwable>> TERMINAL_EXCEPTIONS = Set.of(
    FileNotDownloadedError.class,
    IllegalArgumentException.class,
    ReflectiveOperationException.class,
    JavascriptException.class,
    UnhandledAlertException.class,
    NoSuchSessionException.class,
    UnsupportedCommandException.class
  );

  private static final Set<String> TERMINAL_MESSAGES = Set.of(
    "Reached error page: about:neterror"
  );

  static boolean shouldRetryAfterError(Throwable e) {
    if (TERMINAL_EXCEPTIONS.stream().anyMatch(te -> te.isAssignableFrom(e.getClass()))) return false;
    if (TERMINAL_MESSAGES.stream().anyMatch(message -> e.getMessage().startsWith(message))) return false;

    return e instanceof Exception || e instanceof AssertionError;
  }
}
