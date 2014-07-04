package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.pollingInterval;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byValue;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static com.codeborne.selenide.impl.WebElementProxy.wrap;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;

abstract class AbstractSelenideElement implements InvocationHandler {
  abstract WebElement getDelegate();
  abstract WebElement getActualDelegate() throws NoSuchElementException, IndexOutOfBoundsException;
  abstract String getSearchCriteria();
  protected Exception lastError;

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if ("setValue".equals(method.getName())) {
      setValue((String) args[0]);
      return proxy;
    }
    else if ("val".equals(method.getName())) {
      if (args == null || args.length == 0) {
        return getValue();
      }
      else {
        setValue((String) args[0]);
        return proxy;
      }
    }
    else if ("attr".equals(method.getName())) {
    	return getDelegate().getAttribute((String) args[0]);
    }
    else if ("name".equals(method.getName())) {
    	return getDelegate().getAttribute("name");
    }
    else if ("data".equals(method.getName())) {
    	return getDelegate().getAttribute("data-" + args[0]);
    }
   	else if ("append".equals(method.getName())) {
      append((String) args[0]);
      return proxy;
    }
    else if ("pressEnter".equals(method.getName())) {
      getDelegate().sendKeys(Keys.ENTER);
      return proxy;
    }
    else if ("pressTab".equals(method.getName())) {
      getDelegate().sendKeys(Keys.TAB);
      return proxy;
    }
    else if ("followLink".equals(method.getName())) {
      followLink();
      return null;
    }
    else if ("text".equals(method.getName())) {
      return getDelegate().getText();
    }
    else if ("innerText".equals(method.getName())) {
      return getInnerText();
    }
    else if ("innerHtml".equals(method.getName())) {
      return getInnerHtml();
    }
    else if ("should".equals(method.getName())) {
      return should(proxy, "", (Condition[]) args[0]);
    }
    else if ("shouldHave".equals(method.getName())) {
      return should(proxy, "have ", (Condition[]) args[0]);
    }
    else if ("shouldBe".equals(method.getName())) {
      return should(proxy, "be ", (Condition[]) args[0]);
    }
    else if ("shouldNot".equals(method.getName())) {
      return shouldNot(proxy, "", (Condition[]) args[0]);
    }
    else if ("shouldNotHave".equals(method.getName())) {
      return shouldNot(proxy, "have ", (Condition[]) args[0]);
    }
    else if ("shouldNotBe".equals(method.getName())) {
      return shouldNot(proxy, "be ", (Condition[]) args[0]);
    }
    else if ("parent".equals(method.getName())) {
      return parent((SelenideElement) proxy);
    }
    else if ("closest".equals(method.getName())) {
      return closest((SelenideElement) proxy, (String) args[0]);
    }
    else if ("find".equals(method.getName()) || "$".equals(method.getName())) {
      return args.length == 1 ?
          find((SelenideElement) proxy, args[0], 0) :
          find((SelenideElement) proxy, args[0], (Integer) args[1]);
    }
    else if ("findAll".equals(method.getName()) || "$$".equals(method.getName())) {
      final SelenideElement parent = (SelenideElement) proxy;
      return new ElementsCollection(new BySelectorCollection(parent, getSelector(args[0])));
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
    else if ("is".equals(method.getName()) || "has".equals(method.getName())) {
      return matches((Condition) args[0]);
    }
    else if ("setSelected".equals(method.getName())) {
      setSelected((Boolean) args[0]);
      return proxy;
    }
    else if ("uploadFile".equals(method.getName())) {
      return uploadFile(getDelegate(), (File) args[0]);
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
      waitUntil("", (Condition) args[0], (Long) args[1]);
      return proxy;
    }
    else if ("waitWhile".equals(method.getName())) {
      waitWhile("", (Condition) args[0], (Long) args[1]);
      return proxy;
    }
    else if ("scrollTo".equals(method.getName())) {
      scrollTo();
      return proxy;
    }
    else if ("download".equals(method.getName())) {
      return download();
    }
    else if ("click".equals(method.getName())) {
      click();
      return null;
    }
    else if ("contextClick".equals(method.getName())) {
      contextClick();
      return null;
    }
    else if ("hover".equals(method.getName())) {
      hover();
      return null;
    }
    else if ("dragAndDropTo".equals(method.getName())) {
      dragAndDropTo((String) args[0]);
      return null;
    }
    else if ("getWrappedElement".equals(method.getName())) {
      return getActualDelegate();
    }

    return delegateMethod(getDelegate(), method, args);
  }

  protected boolean matches(Condition condition) {
    try {
      WebElement element = tryToGetElement();
      if (element != null) {
        return condition.apply(element);
      }
    }
    catch (WebDriverException elementNotFound) {
      lastError = elementNotFound;
    }
    catch (IndexOutOfBoundsException ignore) {
      lastError = ignore;
    }

    if (Cleanup.of.isInvalidSelectorError(lastError)) {
      throw Cleanup.of.wrap(lastError);
    }

    return condition.applyNull();
  }

  protected void setSelected(boolean selected) {
    WebElement element = waitForElement();
    if (element.isSelected() ^ selected) {
      element.click();
    }
  }

  protected String getInnerText() {
    WebElement element = waitUntil("", exist, timeout);
    if (isHtmlUnit()) {
      return executeJavaScript("return arguments[0].innerText", element);
    }
    else if (isIE()) {
      return element.getAttribute("innerText");
    }
    return element.getAttribute("textContent");
  }

  protected String getInnerHtml() {
    WebElement element = waitUntil("", exist, timeout);
    if (isHtmlUnit()) {
      return executeJavaScript("return arguments[0].innerHTML", element);
    }
    return element.getAttribute("innerHTML");
  }

  protected WebElement waitForElement() {
    return waitUntil("be ", visible, timeout);
  }

  protected void click() {
    waitForElement().click();
  }

  protected void contextClick() {
    actions().contextClick(waitForElement()).perform();
  }

  protected void hover() {
    actions().moveToElement(waitForElement()).perform();
  }

  protected void dragAndDropTo(String targetCssSelector) {
    SelenideElement target = $(targetCssSelector).shouldBe(visible);
    actions().dragAndDrop(waitForElement(), target).perform();
  }

  protected void followLink() {
    WebElement link = waitForElement();
    String href = link.getAttribute("href");
    link.click();

    // JavaScript $.click() doesn't take effect for <a href>
    if (href != null) {
      open(href);
    }
  }

  protected void setValue(String text) {
    WebElement element = waitForElement();
    if ("select".equalsIgnoreCase(element.getTagName())) {
      selectOptionByValue(element, text);
    }
    else {
      element.clear();
      element.sendKeys(text);
      fireEvent("change");
    }
  }

  protected String getValue() {
    return getDelegate().getAttribute("value");
  }

  protected void append(String text) {
    WebElement element = waitForElement();
    element.sendKeys(text);
    fireEvent("change");
  }

  protected void fireEvent(final String event) {
    final String jsCodeToTriggerEvent
        = "if (document.createEventObject){\n" +  // IE
        "  var evt = document.createEventObject();\n" +
        "  return document.activeElement.fireEvent('on" + event + "', evt);\n" +
        "}\n" +
        "else{\n" +
        "  var evt = document.createEvent('HTMLEvents');\n " +
        "  evt.initEvent('" + event + "', true, true );\n " +
        "  return !document.activeElement.dispatchEvent(evt);\n" +
        '}';
    executeJavaScript(jsCodeToTriggerEvent);
  }

  protected Object should(Object proxy, String prefix, Condition... conditions) {
    for (Condition condition : conditions) {
      waitUntil(prefix, condition, timeout);
    }
    return proxy;
  }

  protected Object shouldNot(Object proxy, String prefix, Condition... conditions) {
    for (Condition condition : conditions) {
      waitWhile(prefix, condition, timeout);
    }
    return proxy;
  }

  protected File uploadFromClasspath(WebElement inputField, String fileName) throws URISyntaxException, IOException {
    URL resource = currentThread().getContextClassLoader().getResource(fileName);
    if (resource == null) {
      throw new IllegalArgumentException("File not found in classpath: " + fileName);
    }
    return uploadFile(inputField, new File(resource.toURI()));
  }
  
  protected File uploadFile(WebElement inputField, File file) throws IOException {
    if (!"input".equalsIgnoreCase(inputField.getTagName())) {
      throw new IllegalArgumentException("Cannot upload file because " + Describe.describe(inputField) + " is not an INPUT");
    }

    if (!file.exists()) {
      throw new IllegalArgumentException("File not found: " + file.getAbsolutePath());
    }

    String canonicalPath = file.getCanonicalPath();
    inputField.sendKeys(canonicalPath);
    return new File(canonicalPath);
  }

  protected void selectOptionByText(WebElement selectField, String optionText) {
    $(selectField).should(exist).find(byText(optionText)).shouldBe(visible);
    new Select(selectField).selectByVisibleText(optionText);
  }

  protected void selectOptionByValue(WebElement selectField, String optionValue) {
    $(selectField).should(exist).find(byValue(optionValue)).shouldBe(visible);
    new Select(selectField).selectByValue(optionValue);
  }

  protected String getSelectedValue(WebElement selectElement) {
    WebElement option = getSelectedOption(selectElement);
    return option == null ? null : option.getAttribute("value");
  }

  protected String getSelectedText(WebElement selectElement) {
    WebElement option = getSelectedOption(selectElement);
    return option == null ? null : option.getText();
  }

  protected SelenideElement getSelectedOption(WebElement selectElement) {
    return wrap(new Select(selectElement).getFirstSelectedOption());
  }

  protected boolean exists() {
    try {
      return getActualDelegate() != null;
    } catch (WebDriverException elementNotFound) {
      if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
        throw Cleanup.of.wrap(elementNotFound);
      }
      return false;
    } catch (IndexOutOfBoundsException invalidElementIndex) {
      return false;
    }
  }

  protected boolean isDisplayed() {
    try {
      WebElement element = getActualDelegate();
      return element != null && element.isDisplayed();
    } catch (WebDriverException elementNotFound) {
      if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
        throw Cleanup.of.wrap(elementNotFound);
      }
      return false;
    } catch (IndexOutOfBoundsException invalidElementIndex) {
      return false;
    }
  }

  protected String describe() {
    try {
      return Describe.describe(getActualDelegate());
    } catch (WebDriverException elementDoesNotExist) {
      return Cleanup.of.webdriverExceptionMessage(elementDoesNotExist);
    } catch (IndexOutOfBoundsException invalidElementIndex) {
      return invalidElementIndex.toString();
    }
  }

  static Object delegateMethod(WebElement delegate, Method method, Object[] args) throws Throwable {
    try {
      return method.invoke(delegate, args);
    } catch (InvocationTargetException e) {
      throw e.getTargetException();
    }
  }

  protected WebElement waitUntil(String prefix, Condition condition, long timeoutMs) {
    final long startTime = currentTimeMillis();
    WebElement element;
    do {
      lastError = null;
      element = tryToGetElement();
      if (element != null) {
        try {
          if (condition.apply(element)) {
            return element;
          }
        }
        catch (WebDriverException elementNotFound) {
          lastError = elementNotFound;
        }
        catch (IndexOutOfBoundsException ignore) {
          lastError = ignore;
        }
      }
      else if (condition.applyNull()) {
        if (Cleanup.of.isInvalidSelectorError(lastError)) {
          throw Cleanup.of.wrap(lastError);
        }
        return null;
      }
      sleep(pollingInterval);
    }
    while (currentTimeMillis() - startTime < timeoutMs);

    if (Cleanup.of.isInvalidSelectorError(lastError)) {
      throw Cleanup.of.wrap(lastError);
    }
    else if (!exists(element)) {
      return throwElementNotFound(condition, timeoutMs);
    }
    else {
      throw new ElementShould(getSearchCriteria(), prefix, condition, element, lastError, timeoutMs);
    }
  }

  protected WebElement throwElementNotFound(Condition condition, long timeoutMs) {
    throw new ElementNotFound(getSearchCriteria(), condition, lastError, timeoutMs);
  }

  protected void waitWhile(String prefix, Condition condition, long timeoutMs) {
    final long startTime = currentTimeMillis();
    WebElement element;
    do {
      lastError = null;
      element = tryToGetElement();
      if (element != null) {
        try {
          if (!condition.apply(element)) {
            return;
          }
        }
        catch (WebDriverException elementNotFound) {
          lastError = elementNotFound;
        }
        catch (IndexOutOfBoundsException ignore) {
          lastError = ignore;
        }
      }
      else if (!condition.applyNull()) {
        if (Cleanup.of.isInvalidSelectorError(lastError)) {
          throw Cleanup.of.wrap(lastError);
        }
        return;
      }
      sleep(pollingInterval);
    }
    while (currentTimeMillis() - startTime < timeoutMs);

    if (Cleanup.of.isInvalidSelectorError(lastError)) {
      throw Cleanup.of.wrap(lastError);
    }
    else if (!exists(element)) {
      throwElementNotFound(not(condition), timeoutMs);
    }
    else {
      throw new ElementShouldNot(getSearchCriteria(), prefix, condition, element, lastError, timeoutMs);
    }
  }

  protected boolean exists(WebElement element) {
    try {
      if (element == null) return false;
      element.isDisplayed();
      return true;
    } catch (WebDriverException elementNotFound) {
      return false;
    }
  }

  protected WebElement tryToGetElement() {
    try {
      return getActualDelegate();
    } catch (WebDriverException elementNotFound) {
      lastError = elementNotFound;
      return null;
    } catch (IndexOutOfBoundsException ignore) {
      lastError = ignore;
      return null;
    } catch (RuntimeException e) {
      throw Cleanup.of.wrap(e);
    }
  }

  protected SelenideElement find(SelenideElement proxy, Object arg, int index) {
    return WaitingSelenideElement.wrap(proxy, getSelector(arg), index);
  }

  protected By getSelector(Object arg) {
    return arg instanceof By ? (By) arg : By.cssSelector((String) arg);
  }

  protected SelenideElement parent(SelenideElement me) {
    return find(me, By.xpath(".."), 0);
  }

  protected SelenideElement closest(SelenideElement me, String tagOrClass) {
    return tagOrClass.startsWith(".") ?
        find(me, By.xpath("ancestor::*[@class='" + tagOrClass.replaceFirst("\\.", "")+ "']"), 0) :
        find(me, By.xpath("ancestor::" + tagOrClass), 0);
  }

  protected void scrollTo() {
    Point location = getDelegate().getLocation();
    executeJavaScript("window.scrollTo(" + location.getX() + ", " + location.getY() + ')');
  }

  protected File download() throws IOException, URISyntaxException {
    return FileDownloader.instance.download(getDelegate());
  }
}
