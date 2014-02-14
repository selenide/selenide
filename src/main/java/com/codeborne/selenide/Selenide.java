package com.codeborne.selenide;

import com.codeborne.selenide.ex.DialogTextMismatch;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.*;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;

import java.net.URL;
import java.util.Collection;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Configuration.dismissModalDialogs;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.WebDriverRunner.*;
import static com.codeborne.selenide.impl.WebElementProxy.wrap;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Selenide {
  public static Navigator navigator = new Navigator();

  /**
   * The main starting point in your tests.
   * Open a browser window with given URL.
   *
   * If browser window was already opened before, it will be reused.
   *
   * Don't bother about closing the browser - it will be closed automatically when all your tests are done.
   *
   * @param relativeOrAbsoluteUrl If starting with "http://" or "https://" or "file://", it's considered to be relative URL. In this case, it's prepended by baseUrl
   */
  public static void open(String relativeOrAbsoluteUrl) {
    navigator.open(relativeOrAbsoluteUrl);
    mockModalDialogs();
  }

  /**
   * @see Selenide#open(String)
   */
  public static void open(URL absoluteUrl) {
    navigator.open(absoluteUrl);
    mockModalDialogs();
  }

  private static boolean doDismissModalDialogs() {
    return isHeadless() || dismissModalDialogs;
  }

  private static void mockModalDialogs() {
    if (doDismissModalDialogs()) {
      String jsCode =
          "  window._selenide_modalDialogReturnValue = true;\n" +
          "  window.alert = function(message) {};\n" +
          "  window.confirm = function(message) {\n" +
          "    return window._selenide_modalDialogReturnValue;\n" +
          "  };";
      executeJavaScript(jsCode);
    }
  }

  /**
   * Open a web page and create PageObject for it.
   * @return PageObject of given class
   */
  public static <PageObjectClass> PageObjectClass open(String relativeOrAbsoluteUrl, Class<PageObjectClass> pageObjectClassClass) {
    open(relativeOrAbsoluteUrl);
    return page(pageObjectClassClass);
  }

  public static <PageObjectClass> PageObjectClass open(URL absoluteUrl, Class<PageObjectClass> pageObjectClassClass) {
    open(absoluteUrl);
    return page(pageObjectClassClass);
  }

  /**
   * Close the browser if it's open
   */
  public static void close() {
    closeWebDriver();
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
   * Take the screenshot of current page and save to file fileName.html and fileName.png
   * @param fileName Name of file (without extension) to save HTML and PNG to
   * @return The name of resulting file
   */
  public static String screenshot(String fileName) {
    return Screenshots.takeScreenShot(fileName);
  }

  /**
   * Wrap standard Selenium WebElement into SelenideElement to use additional methods like shouldHave(), selectOption() etc.
   * @param webElement standard Selenium WebElement
   * @return given WebElement wrapped into SelenideElement
   */
  public static SelenideElement $(WebElement webElement) {
    return wrap(webElement);
  }

  /**
   * Find the first element matching given CSS selector
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @return SelenideElement
   * @throws NoSuchElementException if element was no found
   */
  public static SelenideElement $(String cssSelector) {
    return getElement(By.cssSelector(cssSelector));
  }

  /**
   * Find the first element matching given CSS selector
   * @param seleniumSelector any Selenium selector like By.id(), By.name() etc.
   * @return SelenideElement
   * @throws NoSuchElementException if element was no found
   */
  public static SelenideElement $(By seleniumSelector) {
    return getElement(seleniumSelector);
  }

  /**
   * @see #getElement(By, int)
   */
  public static SelenideElement $(By seleniumSelector, int index) {
    return getElement(seleniumSelector, index);
  }

  /**
   * Find the first element matching given CSS selector
   * @param parent the WebElement to search elements in
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @return SelenideElement
   * @throws NoSuchElementException if element was no found
   */
  public static SelenideElement $(WebElement parent, String cssSelector) {
    return WaitingSelenideElement.wrap($(parent), By.cssSelector(cssSelector), 0);
  }

  /**
   * Find the Nth element matching given criteria
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @param index 0..N
   * @return SelenideElement
   * @throws NoSuchElementException if element was no found
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
   * @throws NoSuchElementException if element was no found
   */
  public static SelenideElement $(WebElement parent, String cssSelector, int index) {
    return WaitingSelenideElement.wrap($(parent), By.cssSelector(cssSelector), index);
  }

  public static SelenideElement $(WebElement parent, By selector) {
    return WaitingSelenideElement.wrap($(parent), selector, 0);
  }

  public static SelenideElement $(WebElement parent, By selector, int index) {
    return WaitingSelenideElement.wrap($(parent), selector, index);
  }

  public static ElementsCollection $$(Collection<? extends WebElement> elements) {
    return new ElementsCollection(new WebElementsCollectionWrapper(elements));
  }

  /**
   * Find all elements matching given CSS selector.
   * Methods returns an ElementsCollection which is a list of WebElement objects that can be iterated,
   * and at the same time is implementation of WebElement interface, meaning that you can call methods .sendKeys(), click() etc. on it.
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @return empty list if element was no found
   */
  public static ElementsCollection $$(String cssSelector) {
    return new ElementsCollection(new BySelectorCollection(By.cssSelector(cssSelector)));
  }

  /**
   * Find all elements matching given CSS selector.
   * Methods returns an ElementsCollection which is a list of WebElement objects that can be iterated,
   * and at the same time is implementation of WebElement interface, meaning that you can call methods .sendKeys(), click() etc. on it.
   * @param seleniumSelector any Selenium selector like By.id(), By.name() etc.
   * @return empty list if element was no found
   */
  public static ElementsCollection $$(By seleniumSelector) {
    return new ElementsCollection(new BySelectorCollection(seleniumSelector));
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
    return new ElementsCollection(new BySelectorCollection(parent, By.cssSelector(cssSelector)));
  }

  /**
   * Find all elements matching given criteria inside given parent element
   * @see Selenide#$$(WebElement, String)
   */
  public static ElementsCollection $$(WebElement parent, By seleniumSelector) {
    return new ElementsCollection(new BySelectorCollection(parent, seleniumSelector));
  }

  /**
   * Find the first element matching given criteria
   * @param criteria instance of By: By.id(), By.className() etc.
   * @return SelenideElement
   * @throws NoSuchElementException if element was no found
   */
  public static SelenideElement getElement(By criteria) {
    return WaitingSelenideElement.wrap(null, criteria, 0);
  }

  /**
   * Find the Nth element matching given criteria
   * @param criteria instance of By: By.id(), By.className() etc.
   * @param index 0..N
   * @return SelenideElement
   * @throws NoSuchElementException if element was no found
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
    return $$(criteria);
  }

  @SuppressWarnings("unchecked")
  public static <T> T executeJavaScript(String jsCode, Object... arguments) {
    return (T) ((JavascriptExecutor) getWebDriver()).executeScript(jsCode, arguments);
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

    throw new ElementNotFound(radioField, value(value), 0);
  }

  public static SelenideElement getSelectedRadio(By radioField) {
    for (WebElement radio : $$(radioField)) {
      if (radio.getAttribute("checked") != null) {
        return wrap(radio);
      }
    }
    return null;
  }

  public static void onConfirmReturn(boolean confirmReturnValue) {
    if (doDismissModalDialogs()) {
      executeJavaScript("window._selenide_modalDialogReturnValue = " + confirmReturnValue + ';');
    }
  }

  /**
   * Accept (Click "Yes" or "Ok") in the confirmation dialog (javascript 'alert' or 'confirm').
   * Method does nothing in case of HtmlUnit browser (since HtmlUnit does not support alerts).
   *
   * @param expectedDialogText if not null, check that confirmation dialog displays this message (case-sensitive)
   * @throws DialogTextMismatch if confirmation message differs from expected message
   */
  public static void confirm(String expectedDialogText) {
    if (!doDismissModalDialogs()) {
      Alert alert = getWebDriver().switchTo().alert();
      String actualDialogText = alert.getText();
      alert.accept();
      checkDialogText(expectedDialogText, actualDialogText);
    }
  }

  /**
   * Dismiss (click "No" or "Cancel") in the confirmation dialog (javascript 'alert' or 'confirm').
   * Method does nothing in case of HtmlUnit browser (since HtmlUnit does not support alerts).
   *
   * @param expectedDialogText if not null, check that confirmation dialog displays this message (case-sensitive)
   * @throws DialogTextMismatch if confirmation message differs from expected message
   */
  public static void dismiss(String expectedDialogText) {
    if (!doDismissModalDialogs()) {
      Alert alert = getWebDriver().switchTo().alert();
      String actualDialogText = alert.getText();
      alert.dismiss();
      checkDialogText(expectedDialogText, actualDialogText);
    }
  }

  private static void checkDialogText(String expectedDialogText, String actualDialogText) {
    if (expectedDialogText != null && !expectedDialogText.equals(actualDialogText)) {
      // TODO Take screenshot
      throw new DialogTextMismatch(actualDialogText, expectedDialogText);
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
   * @see PageFactory#initElements(WebDriver, Class)
   */
  public static <PageObjectClass> PageObjectClass page(Class<PageObjectClass> pageObjectClass) {
    try {
      return page(pageObjectClass.getConstructor().newInstance());
    } catch (Exception e) {
      throw new RuntimeException("Failed to create new instance of " + pageObjectClass, e);
    }
  }

  /**
   * Create a Page Object instance.
   * @see PageFactory#initElements(WebDriver, Class)
   */
  public static <PageObjectClass, T extends PageObjectClass> PageObjectClass page(T pageObject) {
    PageFactory.initElements(new SelenideFieldDecorator(getWebDriver()), pageObject);
    return pageObject;
  }

  public static FluentWait<WebDriver> Wait() {
    return new FluentWait<WebDriver>(getWebDriver())
        .withTimeout(timeout, MILLISECONDS)
        .pollingEvery(Configuration.pollingInterval, MILLISECONDS);
  }

  /**
   * With this method you can use Selenium Actions like described in the
   * <a href="http://code.google.com/p/selenium/wiki/AdvancedUserInteractions">AdvancedUserInteractions</a> page.
   *
   * <pre>
   *   actions()
   *    .sendKeys($(By.name("rememberMe")), "John")
   *    .click($(#rememberMe"))
   *    .click($(byText("Login")))
   *    .build()
   *    .perform();
   * </pre>
   */
  public static Actions actions() {
    return new Actions(getWebDriver());
  }
}
