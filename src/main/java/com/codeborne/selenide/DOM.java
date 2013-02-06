package com.codeborne.selenide;

import com.codeborne.selenide.impl.Describe;
import com.codeborne.selenide.impl.ExtendedFieldDecorator;
import com.codeborne.selenide.impl.ShouldableWebElementProxy;
import com.codeborne.selenide.impl.WebElementWaitingProxy;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Navigation.sleep;
import static com.codeborne.selenide.WebDriverRunner.cleanupWebDriverExceptionMessage;
import static com.codeborne.selenide.WebDriverRunner.fail;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.impl.ShouldableWebElementProxy.wrap;

public class DOM {
  public static long defaultWaitingTimeout = Long.parseLong(System.getProperty("timeout", "4000"));

  /**
   * Wrap standard Selenium WebElement into ShouldableWebElement to use additional methods like shouldHave(), selectOption() etc.
   * @param webElement standard Selenium WebElement
   * @return given WebElement wrapped into ShouldableWebElement
   */
  public static ShouldableWebElement $(WebElement webElement) {
    return ShouldableWebElementProxy.wrap(webElement);
  }

  /**
   * Find the first element matching given CSS selector
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @return ShouldableWebElement
   * @throws NoSuchElementException if element was no found
   */
  public static ShouldableWebElement $(String cssSelector) {
    return getElement(By.cssSelector(cssSelector));
  }

  /**
   * Find the first element matching given CSS selector
   * @param seleniumSelector any Selenium selector like By.id(), By.name() etc.
   * @return ShouldableWebElement
   * @throws NoSuchElementException if element was no found
   */
  public static ShouldableWebElement $(By seleniumSelector) {
    return getElement(seleniumSelector);
  }

  /**
   * @see #getElement(By, int)
   */
  public static ShouldableWebElement $(By seleniumSelector, int index) {
    return getElement(seleniumSelector, index);
  }

  /**
   * Find the first element matching given CSS selector
   * @param parent the WebElement to search elements in
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @return ShouldableWebElement
   * @throws NoSuchElementException if element was no found
   */
  public static ShouldableWebElement $(WebElement parent, String cssSelector) {
    return WebElementWaitingProxy.wrap(parent, By.cssSelector(cssSelector), 0);
  }

  /**
   * Find the Nth element matching given criteria
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @param index 0..N
   * @return ShouldableWebElement
   * @throws NoSuchElementException if element was no found
   */
  public static ShouldableWebElement $(String cssSelector, int index) {
    return WebElementWaitingProxy.wrap(null, By.cssSelector(cssSelector), index);
  }

  /**
   * Find the Nth element matching given criteria
   * @param parent the WebElement to search elements in
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @param index 0..N
   * @return ShouldableWebElement
   * @throws NoSuchElementException if element was no found
   */
  public static ShouldableWebElement $(WebElement parent, String cssSelector, int index) {
    return WebElementWaitingProxy.wrap(parent, By.cssSelector(cssSelector), index);
  }

  /**
   * Find all elements matching given CSS selector.
   * Methods returns an ElementsCollection which is a list of WebElement objects that can be iterated,
   * and at the same time is implementation of WebElement interface, meaning that you can call methods .sendKeys(), click() etc. on it.
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @return empty list if element was no found
   */
  public static ElementsCollection $$(String cssSelector) {
    return new ElementsCollection(getElements(By.cssSelector(cssSelector)));
  }

  /**
   * Find all elements matching given CSS selector.
   * Methods returns an ElementsCollection which is a list of WebElement objects that can be iterated,
   * and at the same time is implementation of WebElement interface, meaning that you can call methods .sendKeys(), click() etc. on it.
   * @param seleniumSelector any Selenium selector like By.id(), By.name() etc.
   * @return empty list if element was no found
   */
  public static ElementsCollection $$(By seleniumSelector) {
    return new ElementsCollection(getElements(seleniumSelector));
  }

  /**
   * Find all elements matching given CSS selector inside given parent element
   * Methods returns an ElementsCollection which is a list of WebElement objects that can be iterated,
   * and at the same time is implementation of WebElement interface, meaning that you can call methods .sendKeys(), click() etc. on it.
   * @param parent the WebElement to search elements in
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @return empty list if element was no found
   */
  public static ElementsCollection $$(WebElement parent, String cssSelector) {
    return new ElementsCollection(parent.findElements(By.cssSelector(cssSelector)));
  }

  /**
   * Find all elements matching given criteria inside given parent element
   * @see DOM#$$(org.openqa.selenium.WebElement, java.lang.String)
   */
  public static ElementsCollection $$(WebElement parent, By seleniumSelector) {
    return new ElementsCollection(parent.findElements(seleniumSelector));
  }

  /**
   * Find the first element matching given criteria
   * @param criteria instance of By: By.id(), By.className() etc.
   * @return ShouldableWebElement
   * @throws NoSuchElementException if element was no found
   */
  public static ShouldableWebElement getElement(By criteria) {
    return WebElementWaitingProxy.wrap(null, criteria, 0);
  }

  /**
   * Find the Nth element matching given criteria
   * @param criteria instance of By: By.id(), By.className() etc.
   * @param index 0..N
   * @return ShouldableWebElement
   * @throws NoSuchElementException if element was no found
   */
  public static ShouldableWebElement getElement(By criteria, int index) {
    return WebElementWaitingProxy.wrap(null, criteria, index);
  }

  /**
   * Find all elements matching given CSS selector
   * @param criteria instance of By: By.id(), By.className() etc.
   * @return empty list if element was no found
   */
  public static ElementsCollection getElements(By criteria) {
    try {
      return new ElementsCollection(getWebDriver().findElements(criteria));
    } catch (WebDriverException e) {
      return fail("Cannot get element " + criteria + ", caused by: " + cleanupWebDriverExceptionMessage(e));
    }
  }
  
  public static void setValue(By by, String value) {
    try {
      WebElement element = getElement(by);
      setValue(element, value);
      triggerChangeEvent(by);
    } catch (WebDriverException e) {
      fail("Cannot get element " + by + ", caused by: " + cleanupWebDriverExceptionMessage(e));
    }
  }

  public static void setValue(By by, int index, String value) {
    try {
      WebElement element = getElement(by, index);
      setValue(element, value);
      triggerChangeEvent(by, index);
    } catch (WebDriverException e) {
      fail("Cannot get element " + by + " and index " + index + ", caused by: " + cleanupWebDriverExceptionMessage(e));
    }
  }

  public static void setValue(WebElement element, String value) {
    element.clear();
    element.sendKeys(value);
  }
  
  public static boolean isJQueryAvailable() {
    Object result = executeJavaScript("return (typeof jQuery);");
    return !"undefined".equalsIgnoreCase(String.valueOf(result));
  }

  public static void click(By by) {
    getElement(by).click();
  }

  /** Calls onclick javascript code, useful for invisible (hovered) elements that cannot be clicked directly */
  public static void callOnClick(By by) {
    executeJavaScript("eval(\"" + getElement(by).getAttribute("onclick") + "\")");
  }

  /**
   * Click the Nth matched element on the page.
   *
   * @param by selector to match element
   * @param index 0..N
   * @throws IllegalArgumentException if index is bigger than number of matched elements.
   */
  public static void click(By by, int index) {
    List<WebElement> matchedElements = getWebDriver().findElements(by);
    if (index < 0 || index >= matchedElements.size()) {
      throw new IllegalArgumentException("Cannot click " + index + "th element: there is " + matchedElements.size() + " elements on the page");
    }

    if (isJQueryAvailable()) {
      executeJQueryMethod(by, "eq(" + index + ").click();");
    } else {
      matchedElements.get(index).click();
    }
  }

  public static void triggerChangeEvent(By by) {
    if (isJQueryAvailable()) {
      executeJQueryMethod(by, "change()");
    }
  }

  public static void triggerChangeEvent(By by, int index) {
    if (isJQueryAvailable())
      executeJQueryMethod(by, "eq(" + index + ").change()");
  }

  static void executeJQueryMethod(By by, String method) {
    String selector = getJQuerySelector(by);
    if (selector != null) {
      executeJavaScript("$(\"" + selector + "\")." + method);
    } else {
      System.err.println("Warning: can't convert " + by + " to JQuery selector, unable to execute " + method);
    }
  }

  static String getJQuerySelector(By seleniumSelector) {
    if (seleniumSelector instanceof By.ByName) {
      String name = seleniumSelector.toString().replaceFirst("By\\.name:\\s*(.*)", "$1");
      return "[name='" + name + "']";
    } else if (seleniumSelector instanceof By.ById) {
      String id = seleniumSelector.toString().replaceFirst("By\\.id:\\s*(.*)", "$1");
      return "#" + id;
    } else if (seleniumSelector instanceof By.ByClassName) {
      String className = seleniumSelector.toString().replaceFirst("By\\.className:\\s*(.*)", "$1");
      return "." + className;
    } else if (seleniumSelector instanceof By.ByCssSelector) {
      return seleniumSelector.toString().replaceFirst("By\\.selector:\\s*(.*)", "$1");
    } else if (seleniumSelector instanceof By.ByXPath) {
      String seleniumXPath = seleniumSelector.toString().replaceFirst("By\\.xpath:\\s*(.*)", "$1");
      return seleniumXPath.replaceFirst("//(.*)", "$1").replaceAll("\\[@", "[");
    }
    return null;
  }

  public static Object executeJavaScript(String jsCode) {
    return ((JavascriptExecutor) getWebDriver()).executeScript(jsCode);
  }

  /**
   * It works only if jQuery "scroll" plugin is included in page being tested
   *
   * @param element HTML element to scroll to.
   */
  public static void scrollTo(By element) {
    if (!isJQueryAvailable()) {
      throw new IllegalStateException("JQuery is not available on current page");
    }
    executeJavaScript("$.scrollTo('" + getJQuerySelector(element) + "')");
  }

  /**
   * Select radio field by value
   * @param radioField any By selector for finding radio field
   * @param value value to select (should match an attribute "value")
   * @return the selected radio field
   */
  public static ShouldableWebElement selectRadio(By radioField, String value) {
    assertEnabled(radioField);
    for (WebElement radio : getElements(radioField)) {
      if (value.equals(radio.getAttribute("value"))) {
        radio.click();
        return wrap(radio);
      }
    }
    throw new NoSuchElementException(radioField + " and value " + value);
  }

  public static ShouldableWebElement getSelectedRadio(By radioField) {
    for (WebElement radio : getElements(radioField)) {
      if (radio.getAttribute("checked") != null) {
        return wrap(radio);
      }
    }
    return null;
  }

  public static String getSelectedValue(By selectField) {
    WebElement option = findSelectedOption(selectField);
    return option == null ? null : option.getAttribute("value");
  }

  public static String getSelectedText(By selectField) {
    WebElement option = findSelectedOption(selectField);
    return option == null ? null : option.getText();
  }

  private static ShouldableWebElement findSelectedOption(By selectField) {
    WebElement selectElement = getElement(selectField);
    List<WebElement> options = selectElement.findElements(By.tagName("option"));
    for (WebElement option : options) {
      if (option.getAttribute("selected") != null) {
        return wrap(option);
      }
    }

    return null;
  }

  public static Select select(By selectField) {
    return new Select(getElement(selectField));
  }

  public static void selectOption(By selectField, String value) {
    select(selectField).selectByValue(value);
  }

  public static void selectOptionByText(By selectField, String text) {
    select(selectField).selectByVisibleText(text);
  }

  public static boolean existsAndVisible(By selector) {
    try {
      return getWebDriver().findElement(selector).isDisplayed();
    } catch (NoSuchElementException doesNotExist) {
      return false;
    }
  }

  public static void followLink(By by) {
    WebElement link = getElement(by);
    String href = link.getAttribute("href");
    link.click();

    // JavaScript $.click() doesn't take effect for <a href>
    if (href != null) {
      Navigation.navigateToAbsoluteUrl(href);
    }
  }

  private static String getActualValue(WebElement element, Condition condition) {
    try {
      return condition.actualValue(element);
    }
    catch (WebDriverException e) {
      return cleanupWebDriverExceptionMessage(e);
    }
  }

  public static ShouldableWebElement assertChecked(By criteria) {
    ShouldableWebElement element = getElement(criteria);
    if (!"true".equalsIgnoreCase(element.getAttribute("checked"))) {
      throw new AssertionError("Element is not checked: " + element);
    }
    return element;
  }

  public static ShouldableWebElement assertNotChecked(By criteria) {
    ShouldableWebElement element = getElement(criteria);
    if (element.getAttribute("checked") != null) {
      throw new AssertionError("Element is checked: " + element);
    }
    return element;
  }

  public static ShouldableWebElement assertDisabled(By criteria) {
    ShouldableWebElement element = getElement(criteria);
    if (!"true".equalsIgnoreCase(element.getAttribute("disabled"))) {
      throw new AssertionError("Element is enabled: " + element);
    }
    return element;
  }

  public static ShouldableWebElement assertEnabled(By criteria) {
    ShouldableWebElement element = getElement(criteria);
    String disabled = element.getAttribute("disabled");
    if (disabled != null && !"false".equalsIgnoreCase(disabled)) {
      throw new AssertionError("Element is disabled: " + element);
    }
    return element;
  }

  public static ShouldableWebElement assertSelected(By criteria) {
    ShouldableWebElement element = getElement(criteria);
    if (!element.isSelected()) {
      throw new AssertionError("Element is not selected: " + element);
    }
    return element;
  }

  public static ShouldableWebElement assertNotSelected(By criteria) {
    ShouldableWebElement element = getElement(criteria);
    if (element.isSelected()) {
      throw new AssertionError("Element is selected: " + element);
    }
    return element;
  }

  public static boolean isVisible(By selector) {
    return getElement(selector).isDisplayed();
  }

  /**
   * out-of-date:
   * Use $(selector).shouldBe(visible);
   */
  public static ShouldableWebElement assertVisible(By selector) {
    return assertElement(selector, visible);
  }

  /**
   * Method fails if element does not exists.
   * out-of-date:
   * Use $(selector).shouldBe(hidden);
   */
  public static ShouldableWebElement assertHidden(By selector) {
    return $(selector).shouldBe(hidden);
  }

  /**
   * out-of-date:
   * Use $(selector).shouldBe(condition);
   */
  public static ShouldableWebElement assertElement(By selector, Condition condition) {
    return $(selector).should(condition);
  }

  /**
   * out-of-date:
   * Use $(selector).shouldBe(condition);
   */
  public static ShouldableWebElement assertElement(WebElement element, Condition condition) {
    return $(element).should(condition);
  }

  public static ShouldableWebElement waitFor(By elementSelector) {
    return waitUntil(elementSelector, 0, visible, defaultWaitingTimeout);
  }

  public static ShouldableWebElement waitFor(String cssSelector) {
    return waitFor(By.cssSelector(cssSelector));
  }

  @Deprecated
  public static ShouldableWebElement waitFor(By elementSelector, Condition condition) {
    return waitUntil(elementSelector, condition);
  }

  public static ShouldableWebElement waitUntil(By elementSelector, Condition condition) {
    return waitUntil(elementSelector, 0, condition, defaultWaitingTimeout);
  }

  public static ShouldableWebElement waitUntil(String cssSelector, Condition condition) {
    return waitUntil(By.cssSelector(cssSelector), condition);
  }

  public static ShouldableWebElement waitUntil(By elementSelector, int index, Condition condition) {
    return waitUntil(elementSelector, index, condition, defaultWaitingTimeout);
  }

  public static ShouldableWebElement waitUntil(String cssSelector, int index, Condition condition) {
    return waitUntil(By.cssSelector(cssSelector), index, condition);
  }

  @Deprecated
  public static ShouldableWebElement waitFor(By elementSelector, Condition condition, long timeoutMs) {
    return waitUntil(elementSelector, condition, timeoutMs);
  }

  public static ShouldableWebElement waitUntil(By elementSelector, Condition condition, long timeoutMs) {
    return waitUntil(elementSelector, 0, condition, timeoutMs);
  }

  public static ShouldableWebElement waitUntil(String cssSelector, Condition condition, long timeoutMs) {
    return waitUntil(By.cssSelector(cssSelector), condition, timeoutMs);
  }

  @Deprecated
  public static ShouldableWebElement waitFor(By elementSelector, int index, Condition condition, long timeoutMs) {
    return waitUntil(elementSelector, index, condition, timeoutMs);
  }

  public static ShouldableWebElement waitUntil(String cssSelector, int index, Condition condition, long timeoutMs) {
    return waitUntil(By.cssSelector(cssSelector), index, condition, timeoutMs);
  }

  public static ShouldableWebElement waitUntil(By elementSelector, int index, Condition condition, long timeoutMs) {
    return waitUntil(null, elementSelector, index, condition, timeoutMs);
  }

  public static ShouldableWebElement waitUntil(WebElement parent, By elementSelector, int index, Condition condition) {
    return waitUntil(parent, elementSelector, index, condition, defaultWaitingTimeout);
  }

  public static ShouldableWebElement waitUntil(WebElement parent, By elementSelector, int index, Condition condition, long timeoutMs) {
    final long startTime = System.currentTimeMillis();
    WebElement element;
    do {
      element = tryToGetElement(parent, elementSelector, index);
      if (element != null) {
        try {
          if (condition.apply(element)) {
            return wrap(element);
          }
        }
        catch (WebDriverException ignore) {
        }
      }
      else if (condition.applyNull()) {
        return null;
      }
      sleep(100);
    }
    while (System.currentTimeMillis() - startTime < timeoutMs);

    return fail("Element " + elementSelector + " hasn't " + condition + " in " + timeoutMs + " ms.;" +
        " actual value: '" + getActualValue(element, condition) + "';" +
        (element == null ? "" : " element details: '" + Describe.describe(element) + "'"));
  }

  private static WebElement tryToGetElement(WebElement parent, By elementSelector, int index) {
    WebElement element;
    try {
      if (index == 0) {
        element = getSearchContext(parent).findElement(elementSelector);
      }
      else {
        List<WebElement> elements = getSearchContext(parent).findElements(elementSelector);
        element = index < elements.size() ? elements.get(index) : null;
      }
    } catch (WebDriverException elementNotFound) {
      element = null;
    }
    return element;
  }

  private static SearchContext getSearchContext(WebElement parent) {
    return parent == null ? getWebDriver() : parent;
  }

  /**
   * Accept (Click "Yes" or "Ok") in the confirmation dialog (javascript 'alert' or 'confirm').
   * Method does nothing in case of HtmlUnit browser (since HtmlUnit does not support alerts).
   *
   * @param expectedConfirmationText if not null, check that confirmation dialog displays this message (case-sensitive)
   * @throws AssertionError if confirmation message differs from expected message
   */
  public static void confirm(String expectedConfirmationText) {
    try {
      Alert alert = checkAlertMessage(expectedConfirmationText);
      alert.accept();
    } catch (UnsupportedOperationException alertIsNotSupportedInHtmlUnit) {
      return;
    }

    waitUntilAlertDisappears();
  }

  /**
   * Dismiss (click "No" or "Cancel") in the confirmation dialog (javascript 'alert' or 'confirm').
   * Method does nothing in case of HtmlUnit browser (since HtmlUnit does not support alerts).
   *
   * @param expectedConfirmationText if not null, check that confirmation dialog displays this message (case-sensitive)
   * @throws AssertionError if confirmation message differs from expected message
   */
  public static void dismiss(String expectedConfirmationText) {
    try {
      Alert alert = checkAlertMessage(expectedConfirmationText);
      alert.dismiss();
    } catch (UnsupportedOperationException alertIsNotSupportedInHtmlUnit) {
      return;
    }

    waitUntilAlertDisappears();
  }

  private static Alert checkAlertMessage(String expectedConfirmationText) {
    Alert alert = getWebDriver().switchTo().alert();
    if (expectedConfirmationText != null && !expectedConfirmationText.equals(alert.getText())) {
      throw new AssertionError("Actual confirmation text is '" + alert.getText() +
          "', but expected: '" + expectedConfirmationText + "'");
    }
    return alert;
  }

  private static void waitUntilAlertDisappears() {
    try {
      long start = System.currentTimeMillis();
      while (getWebDriver().switchTo().alert() != null) {
        getWebDriver().switchTo().alert();
        if (System.currentTimeMillis() - start > defaultWaitingTimeout) {
          fail("Confirmation dialog has not disappeared in " + defaultWaitingTimeout + " milliseconds");
        }
        sleep(100);
      }
    }
    catch (NoAlertPresentException ignore) {
    }
  }

  public static TargetLocator switchTo() {
    return getWebDriver().switchTo();
  }

  /**
   * @deprecated Use $("selector").toString()
   */
  @Deprecated
  public static String describeElement(WebElement element) {
    return Describe.describe(element);
  }

  /**
   * Create a Page Object instance.
   * @see org.openqa.selenium.support.PageFactory#initElements(org.openqa.selenium.WebDriver, java.lang.Class)
   */
  public static <PageObjectClass> PageObjectClass page(Class<PageObjectClass> pageObjectClass) {
    try {
      return page(pageObjectClass.newInstance());
    } catch (Exception e) {
      throw new RuntimeException("Failed to create new instance of " + pageObjectClass, e);
    }
  }

  /**
   * Create a Page Object instance.
   * @see org.openqa.selenium.support.PageFactory#initElements(org.openqa.selenium.WebDriver, java.lang.Class)
   */
  public static <PageObjectClass, T extends PageObjectClass> PageObjectClass page(T pageObject) {
    PageFactory.initElements(new ExtendedFieldDecorator(getWebDriver()), pageObject);
    return pageObject;
  }
}
