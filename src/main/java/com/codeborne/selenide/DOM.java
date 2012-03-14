package com.codeborne.selenide;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static com.codeborne.selenide.WebDriverRunner.fail;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.Navigation.sleep;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class DOM {
  private static final long defaultWaitingTimeout = Long.getLong("timeout", 4000);

  public static WebElement getElement(By by) {
    try {
      return getWebDriver().findElement(by);
    } catch (WebDriverException e) {
      return fail("Cannot get element " + by + ", caused by: " + e);
    }
  }

  public static WebElement getElement(By by, int index) {
    try {
      return getWebDriver().findElements(by).get(index);
    } catch (WebDriverException e) {
      return fail("Cannot get element " + by + ", caused by: " + e);
    }
  }

  public static List<WebElement> getElements(By by) {
    try {
      return getWebDriver().findElements(by);
    } catch (WebDriverException e) {
      return fail("Cannot get element " + by + ", caused by: " + e);
    }
  }
  
  public static void setValue(By by, String value) {
    try {
      WebElement element = getElement(by);
      setValue(element, value);
      triggerChangeEvent(by);
    } catch (WebDriverException e) {
      fail("Cannot get element " + by + ", caused by: " + e);
    }
  }

  public static void setValue(By by, int index, String value) {
    try {
      WebElement element = getElement(by, index);
      setValue(element, value);
      triggerChangeEvent(by, index);
    } catch (WebDriverException e) {
      fail("Cannot get element " + by + " and index " + index + ", caused by: " + e);
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

  /**
   * Click the Nth matched element on the page.
   *
   * @param by selector to match element
   * @param index is zero-based
   * @throws IllegalArgumentException if index is bigger than number of matched elements.
   */
  public static void click(By by, int index) {
    List<WebElement> matchedElements = WebDriverRunner.getWebDriver().findElements(by);
    if (index >= matchedElements.size()) {
      throw new IllegalArgumentException("Cannot click " + index + "th element: there is only " + matchedElements.size() + " elements on the page");
    }

    if (isJQueryAvailable()) {
      executeJavaScript(getJQuerySelector(by) + ".eq(" + index + ").click();");
    } else {
      matchedElements.get(index).click();
    }
  }

  public static void triggerChangeEvent(By by) {
    if (isJQueryAvailable()) {
      executeJavaScript(getJQuerySelector(by) + ".change();");
    }
  }

  public static void triggerChangeEvent(By by, int index) {
    if (isJQueryAvailable()) {
      executeJavaScript(getJQuerySelector(by) + ".eq(" + index + ").change();");
    }
  }

  public static String getJQuerySelector(By seleniumSelector) {
    return "$(\"" + getJQuerySelectorString(seleniumSelector) + "\")";
  }

  public static String getJQuerySelectorString(By seleniumSelector) {
    if (seleniumSelector instanceof By.ByName) {
      String name = seleniumSelector.toString().replaceFirst("By\\.name:\\s*(.*)", "$1");
      return "*[name='" + name + "']";
    } else if (seleniumSelector instanceof By.ById) {
      String id = seleniumSelector.toString().replaceFirst("By\\.id:\\s*(.*)", "$1");
      return "#" + id;
    } else if (seleniumSelector instanceof By.ByClassName) {
      String className = seleniumSelector.toString().replaceFirst("By\\.className:\\s*(.*)", "$1");
      return "." + className;
    } else if (seleniumSelector instanceof By.ByXPath) {
      String seleniumXPath = seleniumSelector.toString().replaceFirst("By\\.xpath:\\s*(.*)", "$1");
      return seleniumXPath.replaceFirst("\\/\\/(.*)", "$1").replaceAll("\\[@", "[");
    }

    return seleniumSelector.toString();
  }

  public static String describeElement(WebElement element) {
    return "<" + element.getTagName() +
        " value=" + element.getAttribute("value") +
        " class=" + element.getAttribute("class") +
        " id=" + element.getAttribute("id") +
        " name=" + element.getAttribute("name") +
        " onclick=" + element.getAttribute("onclick") +
        " onClick=" + element.getAttribute("onClick") +
        " onchange=" + element.getAttribute("onchange") +
        " onChange=" + element.getAttribute("onChange") +
        ">" + element.getText() +
        "</" + element.getTagName() + ">";
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
    executeJavaScript("$.scrollTo('" + getJQuerySelectorString(element) + "')");
  }

  public static WebElement selectRadio(By radioField, String value) {
    assertEnabled(radioField);
    for (WebElement radio : getElements(radioField)) {
      if (value.equals(radio.getAttribute("value"))) {
        radio.click();
        return radio;
      }
    }
    throw new NoSuchElementException("With " + radioField);
  }

  public static String getSelectedValue(By selectField) {
    WebElement option = findSelectedOption(selectField);
    return option == null ? null : option.getAttribute("value");
  }

  public static String getSelectedText(By selectField) {
    WebElement option = findSelectedOption(selectField);
    return option == null ? null : option.getText();
  }

  private static WebElement findSelectedOption(By selectField) {
    WebElement selectElement = getElement(selectField);
    List<WebElement> options = selectElement.findElements(By.tagName("option"));
    for (WebElement option : options) {
      if (option.getAttribute("selected") != null) {
        return option;
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

  public static boolean existsAndVisible(By logoutLink) {
    try {
      return getWebDriver().findElement(logoutLink).isDisplayed();
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
      return e.toString();
    }
  }

  public static void assertChecked(By element) {
    assertThat(getElement(element).getAttribute("checked"), equalTo("true"));
  }

  public static void assertNotChecked(By element) {
    assertNull(getElement(element).getAttribute("checked"));
  }

  public static void assertDisabled(By element) {
    assertThat(getElement(element).getAttribute("disabled"), equalTo("true") );
  }

  public static void assertEnabled(By element) {
    assertThat(getElement(element).getAttribute("disabled"), equalTo("false") );
  }

  public static void assertSelected(By element) {
    assertTrue(getElement(element).isSelected());
  }

  public static void assertNotSelected(By element) {
    assertFalse(getElement(element).isSelected());
  }

  public static boolean isVisible(By selector) {
    return getElement(selector).isDisplayed();
  }

  public static WebElement assertVisible(By selector) {
    return assertElement(selector, Condition.visible);
  }

  /**
   * Method fails if element does not exists.
   */
  public static WebElement assertHidden(By selector) {
    return assertElement(selector, Condition.hidden);
  }

  public static WebElement assertElement(By selector, Condition condition) {
    WebElement element = getElement(selector);
    if (!condition.apply(element)) {
      fail("Element " + selector + " hasn't " + condition + "; actual value is '" + getActualValue(element, condition) + "'");
    }
    return element;
  }

  public static WebElement waitFor(By by) {
    return waitFor(by, 0, Condition.visible, defaultWaitingTimeout);
  }

  public static WebElement waitFor(By by, Condition condition) {
    return waitFor(by, 0, condition, defaultWaitingTimeout);
  }

  public static WebElement waitFor(By by, int index, Condition condition) {
    return waitFor(by, index, condition, defaultWaitingTimeout);
  }

  public static WebElement waitFor(By by, Condition condition, long milliseconds) {
    return waitFor(by, 0, condition, milliseconds);
  }

  public static WebElement waitFor(By by, int index, Condition condition, long milliseconds) {
    final long startTime = System.currentTimeMillis();
    WebElement element = null;
    do {
      try {
        if (index == 0) {
          element = getWebDriver().findElement(by);
        }
        else {
          element = getWebDriver().findElements(by).get(index);
        }

        if (condition.apply(element)) {
          return element;
        }
      } catch (WebDriverException ignored) {
      }
      sleep(50);
    }
    while (System.currentTimeMillis() - startTime < milliseconds);

    fail("Element " + by + " hasn't " + condition + " in " + milliseconds + " ms.; actual value is '" + getActualValue(element, condition) + "'");
    return null;
  }
}
