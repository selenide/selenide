package com.codeborne.selenide.impl;

import com.codeborne.selenide.*;
import com.codeborne.selenide.ex.*;
import com.codeborne.selenide.impl.commands.*;
import org.openqa.selenium.*;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.AssertionMode.SOFT;
import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASSED;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;

class SelenideElementProxy implements InvocationHandler {
  private static final Map<String, Command> commands = new HashMap<>(32);

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

  static {
    commands.put("append", new Append());
    commands.put("click", new Click());
    commands.put("contextClick", new ContextClick());
    commands.put("closest", new GetClosest());
    commands.put("followLink", new FollowLink());
    commands.put("attr", new GetAttribute());
    commands.put("data", new GetDataAttribute());
    commands.put("doubleClick", new DoubleClick());
    commands.put("download", new DownloadFile());
    commands.put("dragAndDropTo", new DragAndDropTo());
    commands.put("exists", new Exists());
    commands.put("find", new Find());
    commands.put("$", new Find());
    commands.put("findAll", new FindAll());
    commands.put("$$", new FindAll());
    commands.put("hover", new Hover());
    commands.put("innerText", new GetInnerText());
    commands.put("innerHtml", new GetInnerHtml());
    commands.put("has", new Matches());
    commands.put("is", new Matches());
    commands.put("isDisplayed", new IsDisplayed());
    commands.put("isImage", new IsImage());
    commands.put("getWrappedElement", new GetWrappedElement());
    commands.put("getSelectedOption", new GetSelectedOption());
    commands.put("getSelectedText", new GetSelectedText());
    commands.put("getSelectedValue", new GetSelectedValue());
    commands.put("getText", new GetText());
    commands.put("name", new GetName());
    commands.put("text", new GetText());
    commands.put("parent", new GetParent());
    commands.put("pressEnter", new PressEnter());
    commands.put("pressEscape", new PressEscape());
    commands.put("pressTab", new PressTab());
    commands.put("screenshot", new TakeScreenshot());
    commands.put("scrollTo", new ScrollTo());
    commands.put("selectOption", new SelectOptionByText());
    commands.put("selectOptionByValue", new SelectOptionByValue());
    commands.put("selectRadio", new SelectRadio());
    commands.put("setSelected", new SetSelected());
    commands.put("setValue", new SetValue());
    
    commands.put("should", new Should(""));
    commands.put("shouldHave", new Should("have "));
    commands.put("shouldBe", new Should("be "));
    commands.put("waitUntil", new Should("be "));
    
    commands.put("shouldNot", new ShouldNot(""));
    commands.put("shouldNotHave", new ShouldNot("have "));
    commands.put("shouldNotBe", new ShouldNot("be "));
    commands.put("waitWhile", new ShouldNot("be "));

    commands.put("uploadFile", new UploadFile());
    commands.put("uploadFromClasspath", new UploadFileFromClasspath());
    commands.put("toString", new ToString());
    commands.put("toWebElement", new ToWebElement());
    commands.put("val", new Val());
  }

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
    if (commands.containsKey(method.getName())) {
      return execute(proxy, method.getName(), args);
    }
    else {
      throw new IllegalArgumentException("Unknown Selenide method: " + method.getName());
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T execute(Object proxy, String methodName, Object[] args) throws IOException {
    Command command = commands.get(methodName);
    return (T) command.execute((SelenideElement) proxy, webElementSource, args);
  }
  
  static Object delegateSeleniumMethod(WebElement delegate, Method method, Object[] args) throws Throwable {
    try {
      return method.invoke(delegate, args);
    } catch (InvocationTargetException e) {
      throw e.getTargetException();
    }
  }
}
