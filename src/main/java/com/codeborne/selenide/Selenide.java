package com.codeborne.selenide;

import com.codeborne.selenide.impl.Navigator;
import com.codeborne.selenide.impl.SelenideFieldDecorator;
import com.codeborne.selenide.impl.WaitingSelenideElement;
import com.codeborne.selenide.impl.WebElementProxy;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;

import java.net.URL;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Configuration.pollingInterval;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.WebDriverRunner.*;
import static com.codeborne.selenide.impl.WebElementProxy.wrap;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Selenide {
  public static Navigator navigator = new Navigator();

  public static void open(String relativeOrAbsoluteUrl) {
    navigator.open(relativeOrAbsoluteUrl);
  }

  public static void open(URL absoluteUrl) {
    navigator.open(absoluteUrl);
  }

  /**
   * Reload current page
   */
  public static void refresh() {
    navigator.open(url());
  }

  public static String title() {
    return getWebDriver().getTitle();
  }

  /**
   * Not recommended. Test should not sleep, but should wait for some condition instead.
   * @param milliseconds Time to sleep in milliseconds
   */
  public static void sleep(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  /**
   * Wrap standard Selenium WebElement into SelenideElement to use additional methods like shouldHave(), selectOption() etc.
   * @param webElement standard Selenium WebElement
   * @return given WebElement wrapped into SelenideElement
   */
  public static SelenideElement $(WebElement webElement) {
    return WebElementProxy.wrap(webElement);
  }

  /**
   * Find the first element matching given CSS selector
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @return SelenideElement
   * @throws org.openqa.selenium.NoSuchElementException if element was no found
   */
  public static SelenideElement $(String cssSelector) {
    return getElement(By.cssSelector(cssSelector));
  }

  /**
   * Find the first element matching given CSS selector
   * @param seleniumSelector any Selenium selector like By.id(), By.name() etc.
   * @return SelenideElement
   * @throws org.openqa.selenium.NoSuchElementException if element was no found
   */
  public static SelenideElement $(By seleniumSelector) {
    return getElement(seleniumSelector);
  }

  /**
   * @see #getElement(org.openqa.selenium.By, int)
   */
  public static SelenideElement $(By seleniumSelector, int index) {
    return getElement(seleniumSelector, index);
  }

  /**
   * Find the first element matching given CSS selector
   * @param parent the WebElement to search elements in
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @return SelenideElement
   * @throws org.openqa.selenium.NoSuchElementException if element was no found
   */
  public static SelenideElement $(WebElement parent, String cssSelector) {
    return WaitingSelenideElement.wrap($(parent), By.cssSelector(cssSelector), 0);
  }

  /**
   * Find the Nth element matching given criteria
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @param index 0..N
   * @return SelenideElement
   * @throws org.openqa.selenium.NoSuchElementException if element was no found
   */
  public static SelenideElement $(String cssSelector, int index) {
    return WaitingSelenideElement.wrap(null, By.cssSelector(cssSelector), index);
  }

  /**
   * Find the Nth element matching given criteria
   * @param parent the WebElement to search elements in
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @param index 0..N
   * @return SelenideElement
   * @throws org.openqa.selenium.NoSuchElementException if element was no found
   */
  public static SelenideElement $(WebElement parent, String cssSelector, int index) {
    return WaitingSelenideElement.wrap($(parent), By.cssSelector(cssSelector), index);
  }

  protected static SelenideElement $(WebElement parent, By selector, int index) {
    return WaitingSelenideElement.wrap($(parent), selector, index);
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
   * @see com.codeborne.selenide.Selenide#$$(org.openqa.selenium.WebElement, String)
   */
  public static ElementsCollection $$(WebElement parent, By seleniumSelector) {
    return new ElementsCollection(parent.findElements(seleniumSelector));
  }

  /**
   * Find the first element matching given criteria
   * @param criteria instance of By: By.id(), By.className() etc.
   * @return SelenideElement
   * @throws org.openqa.selenium.NoSuchElementException if element was no found
   */
  public static SelenideElement getElement(By criteria) {
    return WaitingSelenideElement.wrap(null, criteria, 0);
  }

  /**
   * Find the Nth element matching given criteria
   * @param criteria instance of By: By.id(), By.className() etc.
   * @param index 0..N
   * @return SelenideElement
   * @throws org.openqa.selenium.NoSuchElementException if element was no found
   */
  public static SelenideElement getElement(By criteria, int index) {
    return WaitingSelenideElement.wrap(null, criteria, index);
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

  public static Object executeJavaScript(String jsCode) {
    return ((JavascriptExecutor) getWebDriver()).executeScript(jsCode);
  }

  /**
   * Select radio field by value
   * @param radioField any By selector for finding radio field
   * @param value value to select (should match an attribute "value")
   * @return the selected radio field
   */
  public static SelenideElement selectRadio(By radioField, String value) {
    $(radioField).shouldBe(enabled);
    for (WebElement radio : $$(radioField)) {
      if (value.equals(radio.getAttribute("value"))) {
        radio.click();
        return wrap(radio);
      }
    }
    throw new NoSuchElementException(radioField + " and value " + value);
  }

  public static SelenideElement getSelectedRadio(By radioField) {
    for (WebElement radio : $$(radioField)) {
      if (radio.getAttribute("checked") != null) {
        return wrap(radio);
      }
    }
    return null;
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
        if (System.currentTimeMillis() - start > timeout) {
          fail("Confirmation dialog has not disappeared in " + timeout + " milliseconds");
        }
        sleep(pollingInterval);
      }
    }
    catch (NoAlertPresentException ignore) {
    }
  }

  public static TargetLocator switchTo() {
    return getWebDriver().switchTo();
  }

  public static WebElement getFocusedElement() {
    return (WebElement) executeJavaScript("return document.activeElement");
  }

  /**
   * Create a Page Object instance.
   * @see org.openqa.selenium.support.PageFactory#initElements(org.openqa.selenium.WebDriver, Class)
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
   * @see org.openqa.selenium.support.PageFactory#initElements(org.openqa.selenium.WebDriver, Class)
   */
  public static <PageObjectClass, T extends PageObjectClass> PageObjectClass page(T pageObject) {
    PageFactory.initElements(new SelenideFieldDecorator(getWebDriver()), pageObject);
    return pageObject;
  }

  public static FluentWait<WebDriver> Wait() {
    return new FluentWait<WebDriver>(getWebDriver())
        .withTimeout(Configuration.timeout, MILLISECONDS)
        .pollingEvery(Configuration.pollingInterval, MILLISECONDS);
  }
}
