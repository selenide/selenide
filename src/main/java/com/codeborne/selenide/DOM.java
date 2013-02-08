package com.codeborne.selenide;

import com.codeborne.selenide.impl.Describe;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.WebDriverRunner.*;

public class DOM extends Selenide {

  /**
   * @deprecated Use $(by).setValue(value)
   */
  @Deprecated
  public static void setValue(By by, String value) {
    try {
      WebElement element = $(by);
      setValue(element, value);
      triggerChangeEvent(by);
    } catch (WebDriverException e) {
      fail("Cannot get element " + by + ", caused by: " + cleanupWebDriverExceptionMessage(e));
    }
  }

  /**
   * @deprecated Use $(by, index).setValue(value)
   */
  @Deprecated
  public static void setValue(By by, int index, String value) {
    try {
      WebElement element = $(by, index);
      setValue(element, value);
      triggerChangeEvent(by, index);
    } catch (WebDriverException e) {
      fail("Cannot get element " + by + " and index " + index + ", caused by: " + cleanupWebDriverExceptionMessage(e));
    }
  }

  /**
   * @out-of-date Use $(element).setValue(value)
   *              or $(element).val(value)
   */
  public static void setValue(WebElement element, String value) {
    element.clear();
    element.sendKeys(value);
  }
  
  public static boolean isJQueryAvailable() {
    Object result = executeJavaScript("return (typeof jQuery);");
    return !"undefined".equalsIgnoreCase(String.valueOf(result));
  }

  /**
   * @out-of-date Use $(by).click()
   */
  public static void click(By by) {
    $(by).click();
  }

  /** Calls onclick javascript code, useful for invisible (hovered) elements that cannot be clicked directly */
  public static void callOnClick(By by) {
    executeJavaScript("eval(\"" + $(by).getAttribute("onclick") + "\")");
  }

  /**
   * @out-of-date Use $(by, index).click()
   *
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
   * @out-of-date Use $(selectField).getSelectedValue();
   */
  public static String getSelectedValue(By selectField) {
    return $(selectField).getSelectedValue();
  }

  /**
   * @out-of-date Use $(selectField).getSelectedText();
   */
  public static String getSelectedText(By selectField) {
    return $(selectField).getSelectedText();
  }

  public static Select select(By selectField) {
    return new Select($(selectField));
  }

  /**
   * @out-of-date Use $(selectField).selectOptionByValue(String)
   */
  public static void selectOption(By selectField, String value) {
    select(selectField).selectByValue(value);
  }

  /**
   * @out-of-date Use $(selectField).selectOption(String)
   */
  public static void selectOptionByText(By selectField, String text) {
    select(selectField).selectByVisibleText(text);
  }

  /**
   * @out-of-date Use $(selector).isDisplayed()
   */
  public static boolean existsAndVisible(By selector) {
    try {
      return getWebDriver().findElement(selector).isDisplayed();
    } catch (NoSuchElementException doesNotExist) {
      return false;
    }
  }

  /**
   * @out-of-date Use $(selector).followLink();
   */
  public static void followLink(By selector) {
    $(selector).followLink();
  }

  public static ShouldableWebElement assertChecked(By criteria) {
    ShouldableWebElement element = $(criteria);
    if (!"true".equalsIgnoreCase(element.getAttribute("checked"))) {
      throw new AssertionError("Element is not checked: " + element);
    }
    return element;
  }

  public static ShouldableWebElement assertNotChecked(By criteria) {
    ShouldableWebElement element = $(criteria);
    if (element.getAttribute("checked") != null) {
      throw new AssertionError("Element is checked: " + element);
    }
    return element;
  }

  public static ShouldableWebElement assertDisabled(By criteria) {
    ShouldableWebElement element = $(criteria);
    if (!"true".equalsIgnoreCase(element.getAttribute("disabled"))) {
      throw new AssertionError("Element is enabled: " + element);
    }
    return element;
  }

  public static ShouldableWebElement assertEnabled(By criteria) {
    ShouldableWebElement element = $(criteria);
    String disabled = element.getAttribute("disabled");
    if (disabled != null && !"false".equalsIgnoreCase(disabled)) {
      throw new AssertionError("Element is disabled: " + element);
    }
    return element;
  }

  public static ShouldableWebElement assertSelected(By criteria) {
    ShouldableWebElement element = $(criteria);
    if (!element.isSelected()) {
      throw new AssertionError("Element is not selected: " + element);
    }
    return element;
  }

  public static ShouldableWebElement assertNotSelected(By criteria) {
    ShouldableWebElement element = $(criteria);
    if (element.isSelected()) {
      throw new AssertionError("Element is selected: " + element);
    }
    return element;
  }

  /**
   * @out-of-date Use $(selector).isDisplayed()
   */
  public static boolean isVisible(By selector) {
    return $(selector).isDisplayed();
  }

  /**
   * @out-of-date Use $(selector).shouldBe(visible);
   */
  public static ShouldableWebElement assertVisible(By selector) {
    return assertElement(selector, visible);
  }

  /**
   * @out-of-date Use $(selector).shouldBe(hidden);
   */
  public static ShouldableWebElement assertHidden(By selector) {
    return $(selector).shouldBe(hidden);
  }

  /**
   * @out-of-date Use $(selector).shouldBe(condition);
   */
  public static ShouldableWebElement assertElement(By selector, Condition condition) {
    return $(selector).should(condition);
  }

  /**
   * @out-of-date Use $(selector).shouldBe(condition);
   */
  public static ShouldableWebElement assertElement(WebElement element, Condition condition) {
    return $(element).should(condition);
  }

  /**
   * @out-of-date Use $(elementSelector).shouldBe(visible);
   */
  public static ShouldableWebElement waitFor(By elementSelector) {
    return $(elementSelector).shouldBe(visible);
  }

  /**
   * @out-of-date Use $(cssSelector).shouldBe(visible);
   */
  public static ShouldableWebElement waitFor(String cssSelector) {
    return $(cssSelector).shouldBe(visible);
  }

  /**
   * @deprecated Use $(elementSelector).shouldBe(condition)
   */
  @Deprecated
  public static ShouldableWebElement waitFor(By elementSelector, Condition condition) {
    return waitUntil(elementSelector, condition);
  }

  /**
   * @out-of-date Use $(elementSelector).shouldBe(condition);
   */
  public static ShouldableWebElement waitUntil(By elementSelector, Condition condition) {
    return $(elementSelector).shouldBe(condition);
  }

  /**
   * @out-of-date Use $(cssSelector).shouldBe(condition);
   */
  public static ShouldableWebElement waitUntil(String cssSelector, Condition condition) {
    return $(cssSelector).shouldBe(condition);
  }

  /**
   * @out-of-date Use $(elementSelector, index).shouldBe(condition);
   */
  public static ShouldableWebElement waitUntil(By elementSelector, int index, Condition condition) {
    return $(elementSelector, index).shouldBe(condition);
  }

  /**
   * @out-of-date Use $(cssSelector, index).shouldBe(condition);
   */
  public static ShouldableWebElement waitUntil(String cssSelector, int index, Condition condition) {
    return $(cssSelector, index).shouldBe(condition);
  }

  /**
   * @deprecated Use $(elementSelector).waitUntil(condition, timeoutMs);
   */
  @Deprecated
  public static ShouldableWebElement waitFor(By elementSelector, Condition condition, long timeoutMs) {
    return $(elementSelector).waitUntil(condition, timeoutMs);
  }

  /**
   * @out-of-date Use $(elementSelector).waitUntil(condition, timeoutMs);
   */
  public static ShouldableWebElement waitUntil(By elementSelector, Condition condition, long timeoutMs) {
    return $(elementSelector).waitUntil(condition, timeoutMs);
  }

  /**
   * @out-of-date Use $(cssSelector).waitUntil(condition, timeoutMs);
   */
  public static ShouldableWebElement waitUntil(String cssSelector, Condition condition, long timeoutMs) {
    return $(cssSelector).waitUntil(condition, timeoutMs);
  }

  /**
   * @deprecated Use $(elementSelector, index).waitUntil(condition, timeoutMs);
   */
  @Deprecated
  public static ShouldableWebElement waitFor(By elementSelector, int index, Condition condition, long timeoutMs) {
    return $(elementSelector, index).waitUntil(condition, timeoutMs);
  }

  /**
   * @out-of-date Use $(cssSelector, index).waitUntil(condition, timeoutMs);
   */
  public static ShouldableWebElement waitUntil(String cssSelector, int index, Condition condition, long timeoutMs) {
    return $(cssSelector, index).waitUntil(condition, timeoutMs);
  }

  /**
   * @out-of-date Use $(elementSelector, index).waitUntil(condition, timeoutMs);
   */
  public static ShouldableWebElement waitUntil(By elementSelector, int index, Condition condition, long timeoutMs) {
    return $(elementSelector, index).waitUntil(condition, timeoutMs);
  }

  /**
   * @out-of-date Use $(parent, elementSelector, index).shouldBe(condition);
   */
  public static ShouldableWebElement waitUntil(WebElement parent, By elementSelector, int index, Condition condition) {
    return $(parent, elementSelector, index).shouldBe(condition);
  }

  /**
   * @out-of-date Use $(parent, elementSelector, index).shouldBe(condition);
   */
  public static ShouldableWebElement waitUntil(WebElement parent, By elementSelector, int index, Condition condition, long timeoutMs) {
    return $(parent, elementSelector, index).waitUntil(condition, timeoutMs);
  }

  /**
   * @deprecated Use $("selector").toString()
   */
  @Deprecated
  public static String describeElement(WebElement element) {
    return Describe.describe(element);
  }

}
