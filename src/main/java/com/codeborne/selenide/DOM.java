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
    } catch (WebDriverException e) {
      fail("Cannot get element " + by + " and index " + index + ", caused by: " + cleanupWebDriverExceptionMessage(e));
    }
  }

  /**
   * @deprecated  Use $(element).setValue(value)
   *              or $(element).val(value)
   */
  @Deprecated
  public static void setValue(WebElement element, String value) {
    element.clear();
    element.sendKeys(value);
  }
  
  public static boolean isJQueryAvailable() {
    Object result = executeJavaScript("return (typeof jQuery);");
    return !"undefined".equalsIgnoreCase(String.valueOf(result));
  }

  /**
   * @deprecated Use $(by).click()
   */
  @Deprecated
  public static void click(By by) {
    $(by).click();
  }

  /** Calls onclick javascript code, useful for invisible (hovered) elements that cannot be clicked directly */
  public static void callOnClick(By by) {
    executeJavaScript("eval(\"" + $(by).getAttribute("onclick") + "\")");
  }

  /**
   * @deprecated Use $(by, index).click()
   *
   * Click the Nth matched element on the page.
   *
   * @param by selector to match element
   * @param index 0..N
   * @throws IllegalArgumentException if index is bigger than number of matched elements.
   */
  @Deprecated
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

  @Deprecated
  public static void triggerChangeEvent(By by) {
    if (isJQueryAvailable()) {
      executeJQueryMethod(by, "change()");
    }
  }

  @Deprecated
  public static void triggerChangeEvent(By by, int index) {
    if (isJQueryAvailable())
      executeJQueryMethod(by, "eq(" + index + ").change()");
  }

  @Deprecated
  public static void executeJQueryMethod(By by, String method) {
    String selector = getJQuerySelector(by);
    if (selector != null) {
      executeJavaScript("$(\"" + selector + "\")." + method);
    } else {
      System.err.println("Warning: can't convert " + by + " to JQuery selector, unable to execute " + method);
    }
  }

  @Deprecated
  private static String getJQuerySelector(By seleniumSelector) {
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
  @Deprecated
  public static void scrollTo(By element) {
    if (!isJQueryAvailable()) {
      throw new IllegalStateException("JQuery is not available on current page");
    }
    executeJavaScript("$.scrollTo('" + getJQuerySelector(element) + "')");
  }

  /**
   * @deprecated Use $(selectField).getSelectedValue();
   */
  @Deprecated
  public static String getSelectedValue(By selectField) {
    return $(selectField).getSelectedValue();
  }

  /**
   * @deprecated Use $(selectField).getSelectedText();
   */
  @Deprecated
  public static String getSelectedText(By selectField) {
    return $(selectField).getSelectedText();
  }

  public static Select select(By selectField) {
    return new Select($(selectField));
  }

  /**
   * @deprecated Use $(selectField).selectOptionByValue(String)
   */
  @Deprecated
  public static void selectOption(By selectField, String value) {
    select(selectField).selectByValue(value);
  }

  /**
   * @deprecated Use $(selectField).selectOption(String)
   */
  @Deprecated
  public static void selectOptionByText(By selectField, String text) {
    select(selectField).selectByVisibleText(text);
  }

  /**
   * @deprecated Use $(selector).isDisplayed()
   */
  @Deprecated
  public static boolean existsAndVisible(By selector) {
    try {
      return getWebDriver().findElement(selector).isDisplayed();
    } catch (NoSuchElementException doesNotExist) {
      return false;
    }
  }

  /**
   * @deprecated Use $(selector).followLink();
   */
  @Deprecated
  public static void followLink(By selector) {
    $(selector).followLink();
  }

  /**
   * "checked" attribute seems to work incorrectly in IE or HtmlUnit.
   * @deprecated Use $(criteria).shouldBe(selected)
   */
  @Deprecated
  public static ShouldableWebElement assertChecked(By criteria) {
    ShouldableWebElement element = $(criteria);
    if (!"true".equalsIgnoreCase(element.getAttribute("checked"))) {
      throw new AssertionError("Element is not checked: " + element);
    }
    return element;
  }

  /**
   * "checked" attribute seems to work incorrectly in IE or HtmlUnit.
   * @deprecated Use $(criteria).shouldNotBe(selected)
   */
  @Deprecated
  public static ShouldableWebElement assertNotChecked(By criteria) {
    ShouldableWebElement element = $(criteria);
    if (element.getAttribute("checked") != null) {
      throw new AssertionError("Element is checked: " + element);
    }
    return element;
  }

  /**
   * @deprecated Use $(selector).shouldBe(disabled)
   */
  @Deprecated
  public static ShouldableWebElement assertDisabled(By selector) {
    return $(selector).shouldBe(disabled);
  }

  /**
   * @deprecated Use $(selector).shouldBe(enabled)
   */
  @Deprecated
  public static ShouldableWebElement assertEnabled(By selector) {
    return $(selector).shouldBe(enabled);
  }

  /**
   * @deprecated Use $(selector).shouldBe(selected)
   */
  @Deprecated
  public static ShouldableWebElement assertSelected(By selector) {
    return $(selector).shouldBe(selected);
  }

  /**
   * @deprecated Use $(selector).shouldNotBe(selected)
   */
  @Deprecated
  public static ShouldableWebElement assertNotSelected(By selector) {
    return $(selector).shouldNotBe(selected);
  }

  /**
   * @deprecated Use $(selector).isDisplayed()
   */
  @Deprecated
  public static boolean isVisible(By selector) {
    return $(selector).isDisplayed();
  }

  /**
   * @deprecated Use $(selector).shouldBe(visible);
   */
  @Deprecated
  public static ShouldableWebElement assertVisible(By selector) {
    return assertElement(selector, visible);
  }

  /**
   * @deprecated Use $(selector).shouldBe(hidden);
   */
  @Deprecated
  public static ShouldableWebElement assertHidden(By selector) {
    return $(selector).shouldBe(hidden);
  }

  /**
   * @deprecated Use $(selector).shouldBe(condition);
   */
  @Deprecated
  public static ShouldableWebElement assertElement(By selector, Condition condition) {
    return $(selector).should(condition);
  }

  /**
   * @deprecated Use $(selector).shouldBe(condition);
   */
  @Deprecated
  public static ShouldableWebElement assertElement(WebElement element, Condition condition) {
    return $(element).should(condition);
  }

  /**
   * @deprecated Use $(elementSelector).shouldBe(visible);
   */
  @Deprecated
  public static ShouldableWebElement waitFor(By elementSelector) {
    return $(elementSelector).shouldBe(visible);
  }

  /**
   * @deprecated Use $(cssSelector).shouldBe(visible);
   */
  @Deprecated
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
   * @deprecated Use $(elementSelector).shouldBe(condition);
   */
  @Deprecated
  public static ShouldableWebElement waitUntil(By elementSelector, Condition condition) {
    return $(elementSelector).shouldBe(condition);
  }

  /**
   * @deprecated Use $(cssSelector).shouldBe(condition);
   */
  @Deprecated
  public static ShouldableWebElement waitUntil(String cssSelector, Condition condition) {
    return $(cssSelector).shouldBe(condition);
  }

  /**
   * @deprecated Use $(elementSelector, index).shouldBe(condition);
   */
  @Deprecated
  public static ShouldableWebElement waitUntil(By elementSelector, int index, Condition condition) {
    return $(elementSelector, index).shouldBe(condition);
  }

  /**
   * @deprecated Use $(cssSelector, index).shouldBe(condition);
   */
  @Deprecated
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
   * @deprecated Use $(elementSelector).waitUntil(condition, timeoutMs);
   */
  @Deprecated
  public static ShouldableWebElement waitUntil(By elementSelector, Condition condition, long timeoutMs) {
    return $(elementSelector).waitUntil(condition, timeoutMs);
  }

  /**
   * @deprecated Use $(cssSelector).waitUntil(condition, timeoutMs);
   */
  @Deprecated
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
   * @deprecated Use $(cssSelector, index).waitUntil(condition, timeoutMs);
   */
  @Deprecated
  public static ShouldableWebElement waitUntil(String cssSelector, int index, Condition condition, long timeoutMs) {
    return $(cssSelector, index).waitUntil(condition, timeoutMs);
  }

  /**
   * @deprecated Use $(elementSelector, index).waitUntil(condition, timeoutMs);
   */
  @Deprecated
  public static ShouldableWebElement waitUntil(By elementSelector, int index, Condition condition, long timeoutMs) {
    return $(elementSelector, index).waitUntil(condition, timeoutMs);
  }

  /**
   * @deprecated Use $(parent, elementSelector, index).shouldBe(condition);
   */
  @Deprecated
  public static ShouldableWebElement waitUntil(WebElement parent, By elementSelector, int index, Condition condition) {
    return $(parent, elementSelector, index).shouldBe(condition);
  }

  /**
   * @deprecated Use $(parent, elementSelector, index).shouldBe(condition);
   */
  @Deprecated
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
