package com.codeborne.selenide;

import org.openqa.selenium.*;

import java.util.List;

import static com.codeborne.selenide.WebDriverRunner.fail;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DOM {
  private static final long defaultWaitingTimeout = Long.getLong("timeout", 4000);

  public static WebElement getElement(By by) {
    try {
      return getWebDriver().findElement(by);
    } catch (WebDriverException e) {
      return fail("Cannot get element " + by + ", caused by: " + e);
    }
  }

  public static void setValue(By by, String value) {
    try {
      WebElement element = getWebDriver().findElement(by);
      setValue(element, value);
      triggerChangeEvent(by);
    } catch (WebDriverException e) {
      fail("Cannot get element " + by + ", caused by: " + e);
    }
  }

  public static void setValue(WebElement element, String value) {
    element.clear();
    element.sendKeys(value);
  }
  
  static boolean isJQueryAvailable() {
    Object result = executeJavaScript("return (typeof jQuery);");
    return !"undefined".equalsIgnoreCase(String.valueOf(result));
  }

  public static void click(By by) {
    if (!isJQueryAvailable()) {
      // This doesn't work stably in Windows:
      getElement(by).click();
    }
    else {
      // so we had to create a workaround using JavaScript:
      executeJavaScript(getJQuerySelector(by) + ".click();");
    }
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

  public static void selectRadio(String radioFieldId, String value) {
    String radioButtonId = radioFieldId + value;

    assertThat(getWebDriver().findElements(By.id(radioButtonId)).size(), equalTo(1));
    assertThat(getElement(By.id(radioButtonId)).isDisplayed(), is(true));

    By byXpath = By.xpath("//label[@for='" + radioButtonId + "']");
    assertThat(getWebDriver().findElements(byXpath).size(), equalTo(1));
    assertThat(getElement(byXpath).isDisplayed(), is(true));

    if (isJQueryAvailable()) {
      // It didn't always work properly for us, so we had to add "sleep" as a workaround.
      executeJavaScript(getJQuerySelector(By.id(radioButtonId)) + ".attr('checked', true);");
      sleep(100);
      executeJavaScript(getJQuerySelector(By.id(radioButtonId)) + ".click();");
      sleep(100);
    }
    else {
      //    This doesn't always work properly in Windows:
      click(By.id(radioFieldId + value));
      triggerChangeEvent(By.id(radioFieldId));
    }
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

  public static void selectOption(By selectField, String value) {
    waitFor(selectField, Condition.hasOptions());
    findOptionByValue(selectField, value).click();
    triggerChangeEvent(selectField);
  }

  private static WebElement findOptionByValue(By selectField, String value) {
    try {
      WebElement selectElement = getElement(selectField);
      List<WebElement> options = selectElement.findElements(By.tagName("option"));
      for (WebElement option : options) {
        if (option.getAttribute("value").equals(value)) {
          return option;
        }
      }
    } catch (WebDriverException e) {
      throw new IllegalArgumentException("Option " + value + " is not found for select " + selectField, e);
    }
    throw new IllegalArgumentException("Option " + value + " is not found for select " + selectField);
  }

  public static void selectOptionByText(By selectField, String value) {
    waitFor(selectField, Condition.hasOptions());
    findOptionByText(selectField, value).click();
    triggerChangeEvent(selectField);
  }

  private static WebElement findOptionByText(By selectField, String text) {
    WebElement selectElement = getElement(selectField);
    try {
      return selectElement.findElement(By.xpath("option[text()='" + text + "']"));
    } catch (WebDriverException e) {
      throw new IllegalArgumentException("Option " + text + " is not found for select " + selectField, e);
    }
  }

  /**
   * Not recommended! Searching of unexisting element is veeery slooooow in Selenium.
   * @deprecated
   */
  @Deprecated
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

  public static boolean isVisible(By selector) {
    return getElement(selector).isDisplayed();
  }

  public static WebElement assertVisible(By selector) {
    return assertElement(selector, Condition.visible);
  }

  /**
   * Method fails if element does not exists.
   * Be aware of asserting that element does not exists - it does veeery slooowly in Selenium.
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
    return waitFor(by, Condition.visible);
  }

  public static WebElement waitFor(By by, Condition condition) {
    return waitFor(by, condition, defaultWaitingTimeout);
  }

  public static WebElement waitFor(By by, Condition condition, long milliseconds) {
    final long startTime = System.currentTimeMillis();
    WebElement element = null;
    do {
      try {
        element = getWebDriver().findElement(by);
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

  /**
   * Not recommended. Test should not sleep, but should wait for some condition instead.
   * @param milliseconds Time to sleep in milliseconds
   */
  private static void sleep(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
