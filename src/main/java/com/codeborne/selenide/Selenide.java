package com.codeborne.selenide;

import com.codeborne.selenide.ex.DialogTextMismatch;
import com.codeborne.selenide.ex.JavaScriptErrorsFound;
import com.codeborne.selenide.impl.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.dismissModalDialogs;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.WebDriverRunner.*;
import static com.codeborne.selenide.impl.WebElementProxy.wrap;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * The main starting point of Selenide.
 *
 * You start with methods {@link #open(String)} for opening the tested application page and
 * {@link #$(String)} for searching web elements.
 */
public class Selenide {
  private static final Logger log = Logger.getLogger(Selenide.class.getName());

  public static Navigator navigator = new Navigator();

  /**
   * The main starting point in your tests.
   * Open a browser window with given URL.
   *
   * If browser window was already opened before, it will be reused.
   *
   * Don't bother about closing the browser - it will be closed automatically when all your tests are done.
   *
   * @param relativeOrAbsoluteUrl If not starting with "http://" or "https://" or "file://", it's considered to be relative URL.
   *                              In this case, it's prepended by baseUrl
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
  
  /**
   * Update the hash of the window location (usefull to navigate in ajax apps, since open(url) makes a full page reload).
   * Accept either "#hash" or "hash".
   * @param hash value for window.location.hash
   */
  public static void updateHash(String hash) {
    String localHash = (hash.charAt(0) == '#') ? hash.substring(1) : hash;
    executeJavaScript("window.location.hash='" + localHash + "'");
  }

  private static boolean doDismissModalDialogs() {
    return !supportsModalDialogs() || dismissModalDialogs;
  }

  private static void mockModalDialogs() {
    if (doDismissModalDialogs()) {
      String jsCode =
          "  window._selenide_modalDialogReturnValue = true;\n" +
          "  window.alert = function(message) {};\n" +
          "  window.confirm = function(message) {\n" +
          "    return window._selenide_modalDialogReturnValue;\n" +
          "  };";
      try {
        executeJavaScript(jsCode);
      }
      catch (UnsupportedOperationException cannotExecuteJsAgainstPlainTextPage) {
        log.warning(cannotExecuteJsAgainstPlainTextPage.toString());
      }
    }
  }

  /**
   * Open a web page and create PageObject for it.
   * @return PageObject of given class
   */
  public static <PageObjectClass> PageObjectClass open(String relativeOrAbsoluteUrl,
                                                       Class<PageObjectClass> pageObjectClassClass) {
    open(relativeOrAbsoluteUrl);
    return page(pageObjectClassClass);
  }

  public static <PageObjectClass> PageObjectClass open(URL absoluteUrl,
                                                       Class<PageObjectClass> pageObjectClassClass) {
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

  /**
   * Navigate browser back to previous page
   */
  public static void back() {
    navigator.back();
  }

  /**
   * Navigate browser forward to next page
   */
  public static void forward() {
    navigator.forward();
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
   * Wrap standard Selenium WebElement into SelenideElement
   * to use additional methods like shouldHave(), selectOption() etc.
   *
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
   * and at the same time is implementation of WebElement interface,
   * meaning that you can call methods .sendKeys(), click() etc. on it.
   *
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @return empty list if element was no found
   */
  public static ElementsCollection $$(String cssSelector) {
    return new ElementsCollection(new BySelectorCollection(By.cssSelector(cssSelector)));
  }

  /**
   * Find all elements matching given CSS selector.
   * Methods returns an ElementsCollection which is a list of WebElement objects that can be iterated,
   * and at the same time is implementation of WebElement interface,
   * meaning that you can call methods .sendKeys(), click() etc. on it.
   *
   * @param seleniumSelector any Selenium selector like By.id(), By.name() etc.
   * @return empty list if element was no found
   */
  public static ElementsCollection $$(By seleniumSelector) {
    return new ElementsCollection(new BySelectorCollection(seleniumSelector));
  }

  /**
   * Find all elements matching given CSS selector inside given parent element
   * Methods returns an ElementsCollection which is a list of WebElement objects that can be iterated,
   * and at the same time is implementation of WebElement interface,
   * meaning that you can call methods .sendKeys(), click() etc. on it.
   *
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
   * Not recommended. It's better to use method {@code $(radioField).selectRadio(value);}
   * 
   * Select radio field by value
   * @param radioField any By selector for finding radio field
   * @param value value to select (should match an attribute "value")
   * @return the selected radio field
   */
  public static SelenideElement selectRadio(By radioField, String value) {
    return $(radioField).selectRadio(value);
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
   * @return actual dialog text
   */
  public static String confirm() {
    return confirm(null);
  }
  
  /**
   * Accept (Click "Yes" or "Ok") in the confirmation dialog (javascript 'alert' or 'confirm').
   * Method does nothing in case of HtmlUnit browser (since HtmlUnit does not support alerts).
   *
   * @param expectedDialogText if not null, check that confirmation dialog displays this message (case-sensitive)
   * @throws DialogTextMismatch if confirmation message differs from expected message
   * @return actual dialog text
   */
  public static String confirm(String expectedDialogText) {
    if (!doDismissModalDialogs()) {
      Alert alert = waitForAlert();
      String actualDialogText = alert.getText();
      alert.accept();
      checkDialogText(expectedDialogText, actualDialogText);
      return actualDialogText;
    }
    return null;
  }
  
  private static Alert waitForAlert() {
    final long startTime = currentTimeMillis();
    NoAlertPresentException lastError;
    do {
      try {
        Alert alert = getWebDriver().switchTo().alert();
        alert.getText(); // check that alert actually exists
        return alert;
      }
      catch (NoAlertPresentException e) {
        lastError = e;
      }
    }
    while (currentTimeMillis() - startTime <= timeout);
    
    throw lastError;
  }

  /**
   * Dismiss (click "No" or "Cancel") in the confirmation dialog (javascript 'alert' or 'confirm').
   * @return actual dialog text
   */
  public static String dismiss() {
    return dismiss(null);
  }

  /**
   * Dismiss (click "No" or "Cancel") in the confirmation dialog (javascript 'alert' or 'confirm').
   * Method does nothing in case of HtmlUnit browser (since HtmlUnit does not support alerts).
   *
   * @param expectedDialogText if not null, check that confirmation dialog displays this message (case-sensitive)
   * @throws DialogTextMismatch if confirmation message differs from expected message
   * @return actual dialog text
   */
  public static String dismiss(String expectedDialogText) {
    if (!doDismissModalDialogs()) {
      Alert alert = waitForAlert();
      String actualDialogText = alert.getText();
      alert.dismiss();
      checkDialogText(expectedDialogText, actualDialogText);
      return actualDialogText;
    }
    return null;
  }

  private static void checkDialogText(String expectedDialogText, String actualDialogText) {
    if (expectedDialogText != null && !expectedDialogText.equals(actualDialogText)) {
      Screenshots.takeScreenShot(Selenide.class.getName(), Thread.currentThread().getName());
      throw new DialogTextMismatch(actualDialogText, expectedDialogText);
    }
  }

  public static SelenideTargetLocator switchTo() {
    return new SelenideTargetLocator(getWebDriver().switchTo());
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

  /**
   * Switch to window/tab by title
   * @deprecated Same as switchTo().window(title)
   */
  @Deprecated
  public static void switchToWindow(String title) {
    switchTo().window(title);
  }

  /**
   * @deprecated The same as {@code switchTo().window(index);}
   * @param index index of window (0-based)
   */
  @Deprecated
  public static void switchToWindow(int index) {
    switchTo().window(index);
  }
  
  /**
   * Get JavaScript errors that happened on this page.
   *
   * Format can differ from browser to browser:
   *  - Uncaught ReferenceError: $ is not defined at http://localhost:35070/page_with_js_errors.html:8
   *  - ReferenceError: Can't find variable: $ at http://localhost:8815/page_with_js_errors.html:8
   *
   * Function returns nothing if the page has its own "window.onerror" handler.
   *
   * @return list of error messages. Returns empty list if webdriver is not started properly. 
   */
  public static List<String> getJavascriptErrors() {
    if (!WebDriverRunner.webdriverContainer.hasWebDriverStarted()) {
      return emptyList();
    }
    try {
      List<Object> errors = executeJavaScript("return window._selenide_jsErrors");
      if (errors == null || errors.isEmpty()) {
        return emptyList();
      }
      List<String> result = new ArrayList<String>(errors.size());
      for (Object error : errors) {
        result.add(error.toString());
      }
      return result;
    } catch (WebDriverException cannotExecuteJs) {
      log.severe(cannotExecuteJs.toString());
      return emptyList();
    }
  }

  /**
   * Check if there is not JS errors on the page
   * @throws JavaScriptErrorsFound
   */
  public static void assertNoJavascriptErrors() throws JavaScriptErrorsFound {
    List<String> jsErrors = getJavascriptErrors();
    if (jsErrors != null && !jsErrors.isEmpty()) {
      throw new JavaScriptErrorsFound(jsErrors);
    }
  }

  /**
   * Zoom current page (in or out).
   * @param factor e.g. 1.1 or 2.0 or 0.5
   */
  public static void zoom(double factor) {
    executeJavaScript(
        "document.body.style.transform = 'scale(' + arguments[0] + ')';" +
        "document.body.style.transformOrigin = '0 0';",
        factor
    );
  }

  /**
   * Same as com.codeborne.selenide.Selenide#getWebDriverLogs(java.lang.String, java.util.logging.Level)
   * 
   * EXPERIMENTAL! Use with caution.
   */
  public static List<String> getWebDriverLogs(String logType) {
    return getWebDriverLogs(logType, Level.ALL);
  }

  /**
   * EXPERIMENTAL! Use with caution.
   * 
   * Getting and filtering of the WebDriver logs for specified LogType by specified logging level
   * <br />
   * For example to get WebDriver Browser's console output (including JS info, warnings, errors, etc. messages)
   * you can use:
   * <br />
   * <pre>
   *   {@code
   *     for(String logEntry : getWebDriverLogs(LogType.BROWSER, Level.ALL)) {
   *       Reporter.log(logEntry + "<br />");
   *     }
   *   }
   * </pre>
   * <br />
   * Be aware that currently "manage().logs()" is in the Beta stage, but it is beta-then-nothing :)
   * <br />
   * List of the unsupported browsers and issues:
   * <br />
   * http://bit.ly/RZcmrM
   * <br />
   * http://bit.ly/1nZTaqu
   * <br />
   *
   * @param logType WebDriver supported log types
   * @param logLevel logging level that will be used to control logging output
   * @return list of log entries
   * @see org.openqa.selenium.logging.LogType,
   * @see java.util.logging.Level
   */
  public static List<String> getWebDriverLogs(String logType, Level logLevel) {
    return listToString(getLogEntries(logType, logLevel));
  }

  private static List<LogEntry> getLogEntries(String logType, Level logLevel) {
    try {
      return getWebDriver().manage().logs().get(logType).filter(logLevel);
    }
    catch (UnsupportedOperationException ignore) {
      return emptyList();
    }
  }

  private static <T> List<String> listToString(List<T> objects) {
    if (objects == null || objects.isEmpty()) {
      return emptyList();
    }
    List<String> result = new ArrayList<String>(objects.size());
    for (T object : objects) {
      result.add(object.toString());
    }
    return result;
  }
}
