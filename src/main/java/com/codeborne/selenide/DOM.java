package com.codeborne.selenide;

import com.codeborne.selenide.impl.Describe;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static com.codeborne.selenide.Condition.*;

/**
 * @deprecated Use methods of class Selenide - these are useful.
 * All methods of class DOM are a bullshit for historical reasons.
 */
@Deprecated
public class DOM extends Selenide {
  private static JQuery jQuery = new JQuery();

  /**
   * @deprecated Use $(by).setValue(value)
   */
  @Deprecated
  public static void setValue(By by, String value) {
    $(by).setValue(value);
  }

  /**
   * @deprecated Use $(by, index).setValue(value)
   */
  @Deprecated
  public static void setValue(By by, int index, String value) {
    $(by, index).setValue(value);
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

  /**
   * @deprecated Use JQuery.isJQueryAvailable
   */
  @Deprecated
  public static boolean isJQueryAvailable() {
    return jQuery.isJQueryAvailable();
  }

  /**
   * @deprecated Use $(by).click()
   */
  @Deprecated
  public static void click(By by) {
    $(by).click();
  }

  /**
   * @deprecated Use JQuery.callOnClick
   */
  @Deprecated
  public static void callOnClick(By by) {
    jQuery.onClick(by);
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
    $(by, index).click();
  }

  /**
   * @deprecated Use JQuery.triggerChangeEvent
   */
  @Deprecated
  public static void triggerChangeEvent(By by) {
    jQuery.change(by);
  }

  /**
   * @deprecated Use JQuery.triggerChangeEvent
   */
  @Deprecated
  public static void triggerChangeEvent(By by, int index) {
    jQuery.change(by, index);
  }

  /**
   * @deprecated Use JQuery.scrollTo
   */
  @Deprecated
  public static void scrollTo(By element) {
    jQuery.scrollTo(element);
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
    return $(selector).isDisplayed();
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
  public static SelenideElement assertChecked(By criteria) {
    SelenideElement element = $(criteria);
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
  public static SelenideElement assertNotChecked(By criteria) {
    SelenideElement element = $(criteria);
    if (element.getAttribute("checked") != null) {
      throw new AssertionError("Element is checked: " + element);
    }
    return element;
  }

  /**
   * @deprecated Use $(selector).shouldBe(disabled)
   */
  @Deprecated
  public static SelenideElement assertDisabled(By selector) {
    return $(selector).shouldBe(disabled);
  }

  /**
   * @deprecated Use $(selector).shouldBe(enabled)
   */
  @Deprecated
  public static SelenideElement assertEnabled(By selector) {
    return $(selector).shouldBe(enabled);
  }

  /**
   * @deprecated Use $(selector).shouldBe(selected)
   */
  @Deprecated
  public static SelenideElement assertSelected(By selector) {
    return $(selector).shouldBe(selected);
  }

  /**
   * @deprecated Use $(selector).shouldNotBe(selected)
   */
  @Deprecated
  public static SelenideElement assertNotSelected(By selector) {
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
  public static SelenideElement assertVisible(By selector) {
    return $(selector).shouldBe(visible);
  }

  /**
   * @deprecated Use $(selector).shouldBe(hidden);
   */
  @Deprecated
  public static SelenideElement assertHidden(By selector) {
    return $(selector).shouldBe(hidden);
  }

  /**
   * @deprecated Use $(selector).shouldBe(condition);
   */
  @Deprecated
  public static SelenideElement assertElement(By selector, Condition condition) {
    return $(selector).should(condition);
  }

  /**
   * @deprecated Use $(selector).shouldBe(condition);
   */
  @Deprecated
  public static SelenideElement assertElement(WebElement element, Condition condition) {
    return $(element).should(condition);
  }

  /**
   * @deprecated Use $(elementSelector).shouldBe(visible) or $(elementSelector).should(appear)
   */
  @Deprecated
  public static SelenideElement waitFor(By elementSelector) {
    return $(elementSelector).shouldBe(visible);
  }

  /**
   * @deprecated Use $(cssSelector).shouldBe(visible) or $(cssSelector).should(appear)
   */
  @Deprecated
  public static SelenideElement waitFor(String cssSelector) {
    return $(cssSelector).shouldBe(visible);
  }

  /**
   * @deprecated Use $(elementSelector).shouldBe(condition)
   */
  @Deprecated
  public static SelenideElement waitFor(By elementSelector, Condition condition) {
    return waitUntil(elementSelector, condition);
  }

  /**
   * @deprecated Use $(elementSelector).shouldBe(condition);
   */
  @Deprecated
  public static SelenideElement waitUntil(By elementSelector, Condition condition) {
    return $(elementSelector).shouldBe(condition);
  }

  /**
   * @deprecated Use $(cssSelector).shouldBe(condition);
   */
  @Deprecated
  public static SelenideElement waitUntil(String cssSelector, Condition condition) {
    return $(cssSelector).shouldBe(condition);
  }

  /**
   * @deprecated Use $(elementSelector, index).shouldBe(condition);
   */
  @Deprecated
  public static SelenideElement waitUntil(By elementSelector, int index, Condition condition) {
    return $(elementSelector, index).shouldBe(condition);
  }

  /**
   * @deprecated Use $(cssSelector, index).shouldBe(condition);
   */
  @Deprecated
  public static SelenideElement waitUntil(String cssSelector, int index, Condition condition) {
    return $(cssSelector, index).shouldBe(condition);
  }

  /**
   * @deprecated Use $(elementSelector).waitUntil(condition, timeoutMs);
   */
  @Deprecated
  public static SelenideElement waitFor(By elementSelector, Condition condition, long timeoutMs) {
    return $(elementSelector).waitUntil(condition, timeoutMs);
  }

  /**
   * @deprecated Use $(elementSelector).waitUntil(condition, timeoutMs);
   */
  @Deprecated
  public static SelenideElement waitUntil(By elementSelector, Condition condition, long timeoutMs) {
    return $(elementSelector).waitUntil(condition, timeoutMs);
  }

  /**
   * @deprecated Use $(cssSelector).waitUntil(condition, timeoutMs);
   */
  @Deprecated
  public static SelenideElement waitUntil(String cssSelector, Condition condition, long timeoutMs) {
    return $(cssSelector).waitUntil(condition, timeoutMs);
  }

  /**
   * @deprecated Use $(elementSelector, index).waitUntil(condition, timeoutMs);
   */
  @Deprecated
  public static SelenideElement waitFor(By elementSelector, int index, Condition condition, long timeoutMs) {
    return $(elementSelector, index).waitUntil(condition, timeoutMs);
  }

  /**
   * @deprecated Use $(cssSelector, index).waitUntil(condition, timeoutMs);
   */
  @Deprecated
  public static SelenideElement waitUntil(String cssSelector, int index, Condition condition, long timeoutMs) {
    return $(cssSelector, index).waitUntil(condition, timeoutMs);
  }

  /**
   * @deprecated Use $(elementSelector, index).waitUntil(condition, timeoutMs);
   */
  @Deprecated
  public static SelenideElement waitUntil(By elementSelector, int index, Condition condition, long timeoutMs) {
    return $(elementSelector, index).waitUntil(condition, timeoutMs);
  }

  /**
   * @deprecated Use $(parent, elementSelector, index).shouldBe(condition);
   */
  @Deprecated
  public static SelenideElement waitUntil(WebElement parent, By elementSelector, int index, Condition condition) {
    return $(parent, elementSelector, index).shouldBe(condition);
  }

  /**
   * @deprecated Use $(parent, elementSelector, index).shouldBe(condition);
   */
  @Deprecated
  public static SelenideElement waitUntil(WebElement parent, By elementSelector, int index, Condition condition, long timeoutMs) {
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
