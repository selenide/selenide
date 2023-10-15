package com.codeborne.selenide;

import com.codeborne.selenide.ex.DialogTextMismatch;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.SessionId;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import static com.codeborne.selenide.ModalOptions.withExpectedText;
import static com.codeborne.selenide.WebDriverRunner.getSelenideDriver;
import static com.codeborne.selenide.logevents.SelenideLogger.getReadableSubject;

/**
 * The main starting point of Selenide.
 * <p>
 * You start with methods {@link #open(String)} for opening the tested application page and
 * {@link #$(String)} for searching web elements.
 */
@ParametersAreNonnullByDefault
public class Selenide {

  /**
   * The main starting point in your tests.
   * Open a browser window with given URL.
   * <p>
   * If browser window was already opened before, it will be reused.
   * <p>
   * Don't bother about closing the browser - it will be closed automatically when all your tests are done.
   *
   * @param relativeOrAbsoluteUrl If not starting with "http://" or "https://" or "file://", it's considered to be relative URL.
   *                              In this case, it's prepended by baseUrl
   */
  public static void open(String relativeOrAbsoluteUrl) {
    getSelenideDriver().open(relativeOrAbsoluteUrl);
  }

  /**
   * @see Selenide#open(String)
   */
  public static void open(URL absoluteUrl) {
    getSelenideDriver().open(absoluteUrl);
  }

  /**
   * The main starting point in your tests.
   * <p>
   * Open a browser window with given URL and credentials for basic authentication
   * <p>
   * If browser window was already opened before, it will be reused.
   * <p>
   * Don't bother about closing the browser - it will be closed automatically when all your tests are done.
   * <p>
   * If not starting with "http://" or "https://" or "file://", it's considered to be relative URL.
   * <p>
   * In this case, it's prepended by baseUrl
   *
   * @param domain Name of domain to apply Basic Auth.
   *               1. If empty, Basic Auth will be applied to all domains.
   *               2. If non-empty, Basic Auth will be applied only to URLs containing this domain.
   *                 2.1. May contain multiple domain names (delimited by "," or "|").
   */
  public static void open(String relativeOrAbsoluteUrl, String domain, String login, String password) {
    getSelenideDriver().open(relativeOrAbsoluteUrl, domain, login, password);
  }

  /**
   * The main starting point in your tests.
   * <p>
   * Open browser and pass authentication using build-in proxy.
   * <p>
   * A common authenticationType is "Basic". See Web HTTP reference for other types.
   * <p>
   * This method can only work if - {@code Configuration.proxyEnabled == true}
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Proxy-Authorization">Web HTTP reference</a>
   * @see AuthenticationType
   * @deprecated AFAIK "login+password" combination is only used in BASIC authentication. If you need basic auth, use
   * {@link #open(String, AuthenticationType, Credentials)} with {@link BasicAuthCredentials} parameter.
   */
  @Deprecated
  public static void open(String relativeOrAbsoluteUrl, AuthenticationType authenticationType, String login, String password) {
    Credentials credentials = new BasicAuthCredentials(login, password);
    open(relativeOrAbsoluteUrl, authenticationType, credentials);
  }

  /**
   * The main starting point in your tests.
   * <p>
   * Open browser and pass authentication using build-in proxy.
   * <p>
   * A common authenticationType is "Basic". See Web HTTP reference for other types.
   * <p>
   * This method can only work if - {@code Configuration.proxyEnabled == true}
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Proxy-Authorization">Web HTTP reference</a>
   * @see AuthenticationType
   * @see Credentials
   */
  public static void open(String relativeOrAbsoluteUrl, AuthenticationType authenticationType, Credentials credentials) {
    getSelenideDriver().open(relativeOrAbsoluteUrl, authenticationType, credentials);
  }

  /**
   * @see Selenide#open(String, String, String, String)
   */
  public static void open(URL absoluteUrl, String domain, String login, String password) {
    getSelenideDriver().open(absoluteUrl, domain, login, password);
  }

  /**
   * Open an empty browser (without opening any pages).
   * E.g. useful for starting mobile applications in Appium.
   */
  public static void open() {
    getSelenideDriver().open();
  }

  /**
   * @since 5.23.0
   */
  @CheckReturnValue
  @Nonnull
  public static Conditional<WebDriver> webdriver() {
    return getSelenideDriver().webdriver();
  }

  public static void using(WebDriver webDriver, Runnable lambda) {
    WebDriverRunner.using(webDriver, lambda);
  }

  public static void using(WebDriver webDriver, SelenideProxyServer proxy, Runnable lambda) {
    WebDriverRunner.using(webDriver, proxy, lambda);
  }

  /**
   * Open a new browser (with the same settings as the default browser),
   * and run given code block in this browser.
   * <p>
   *
   * In the end, the browser will be closed.
   * @since 6.13.0
   */
  public static void inNewBrowser(Runnable lambda) {
    WebDriverRunner.inNewBrowser(lambda);
  }

  /**
   * Update the hash of the window location.
   * Useful to navigate in ajax apps without reloading the page, since open(url) makes a full page reload.
   *
   * @param hash value for window.location.hash - Accept either "#hash" or "hash".
   */
  public static void updateHash(String hash) {
    getSelenideDriver().updateHash(hash);
  }

  /**
   * Open a web page and create PageObject for it.
   *
   * @return PageObject of given class
   */
  @CheckReturnValue
  @Nonnull
  public static <PageObjectClass> PageObjectClass open(String relativeOrAbsoluteUrl,
                                                       Class<PageObjectClass> pageObjectClassClass) {
    return getSelenideDriver().open(relativeOrAbsoluteUrl, pageObjectClassClass);
  }

  /**
   * Open a web page and create PageObject for it.
   *
   * @return PageObject of given class
   */
  @CheckReturnValue
  @Nonnull
  public static <PageObjectClass> PageObjectClass open(URL absoluteUrl,
                                                       Class<PageObjectClass> pageObjectClassClass) {
    return getSelenideDriver().open(absoluteUrl, pageObjectClassClass);
  }

  /**
   * Open a web page using Basic Auth credentials and create PageObject for it.
   *
   * @return PageObject of given class
   */
  @CheckReturnValue
  @Nonnull
  public static <PageObjectClass> PageObjectClass open(String relativeOrAbsoluteUrl,
                                                       String domain, String login, String password,
                                                       Class<PageObjectClass> pageObjectClassClass) {
    return getSelenideDriver().open(relativeOrAbsoluteUrl, domain, login, password, pageObjectClassClass);
  }

  /**
   * Open a web page using Basic Auth credentials and create PageObject for it.
   *
   * @return PageObject of given class
   */
  @CheckReturnValue
  @Nonnull
  public static <PageObjectClass> PageObjectClass open(URL absoluteUrl, String domain, String login, String password,
                                                       Class<PageObjectClass> pageObjectClassClass) {
    return getSelenideDriver().open(absoluteUrl, domain, login, password, pageObjectClassClass);
  }

  /**
   * Close the current window, quitting the browser if it's the last window currently open.
   *
   * @see WebDriver#close()
   */
  public static void closeWindow() {
    SelenideLogger.run("current window", getReadableSubject("close"), WebDriverRunner::closeWindow);
  }

  /**
   * <p>Close the browser if it's open.</p>
   * <br>
   * <p>NB! Method quits this driver, closing every associated window.</p>
   *
   * @see WebDriver#quit()
   */
  public static void closeWebDriver() {
    SelenideLogger.run("webdriver", getReadableSubject("close"), WebDriverRunner::closeWebDriver);
  }

  /**
   * Reload current page
   */
  public static void refresh() {
    getSelenideDriver().refresh();
  }

  /**
   * Navigate browser back to previous page
   */
  public static void back() {
    getSelenideDriver().back();
  }

  /**
   * Navigate browser forward to next page
   */
  public static void forward() {
    getSelenideDriver().forward();
  }

  /**
   * @return title of the page
   */
  @CheckReturnValue
  @Nullable
  public static String title() {
    return getSelenideDriver().title();
  }

  /**
   * <p>
   * Not recommended. Test should not sleep, but should wait for some condition instead.
   * </p>
   * <p>
   * Implementation detail: method {@link java.lang.Thread#sleep(long)} is not guaranteed to
   * sleep exactly given number of milliseconds, it can awake earlier. That's why we need to use a
   * loop to guarantee the sleep duration.
   * </p>
   *
   * @param milliseconds Time to sleep in milliseconds
   */
  public static void sleep(long milliseconds) {
    Stopwatch.sleepAtLeast(milliseconds);
  }

  /**
   * Take the screenshot of current page and save to file fileName.html and fileName.png
   *
   * @param fileName Name of file (without extension) to save HTML and PNG to
   * @return The name of resulting file
   */
  @CheckReturnValue
  @Nullable
  public static String screenshot(String fileName) {
    return getSelenideDriver().screenshot(fileName);
  }

  /**
   * Take the screenshot of current page and return it.
   *
   * @param outputType type of the returned screenshot
   * @return The screenshot (as bytes, base64 or temporary file)
   * or null if webdriver does not support taking screenshots.
   */
  @CheckReturnValue
  @Nullable
  public static <T> T screenshot(OutputType<T> outputType) {
    return getSelenideDriver().screenshot(outputType);
  }

  /**
   * Wrap standard Selenium WebElement into SelenideElement
   * to use additional methods like {@link SelenideElement#shouldHave(Condition...)},
   * {@link SelenideElement#selectOption(String, String...)} etc.
   *
   * @param webElement standard Selenium WebElement
   * @return given WebElement wrapped into SelenideElement
   */
  @CheckReturnValue
  @Nonnull
  public static SelenideElement $(WebElement webElement) {
    return getSelenideDriver().$(webElement);
  }

  /**
   * Locates the first element matching given CSS selector (lazy evaluation)
   *
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @return SelenideElement
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public static SelenideElement $(String cssSelector) {
    return getSelenideDriver().find(cssSelector);
  }

  /**
   * Locates the first element matching given XPATH expression (lazy evaluation)
   *
   * @param xpathExpression any XPATH expression //*[@id='value'] //E[contains(@A, 'value')]
   * @return SelenideElement which locates elements via XPath
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public static SelenideElement $x(String xpathExpression) {
    return getSelenideDriver().$x(xpathExpression);
  }

  /**
   * Locates the first element matching given CSS selector (lazy evaluation)
   *
   * @param seleniumSelector any Selenium selector like By.id(), By.name() etc.
   * @return SelenideElement
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public static SelenideElement $(By seleniumSelector) {
    return getSelenideDriver().find(seleniumSelector);
  }

  /**
   * @see #element(By, int)
   */
  @CheckReturnValue
  @Nonnull
  public static SelenideElement $(By seleniumSelector, int index) {
    return getSelenideDriver().find(seleniumSelector, index);
  }

  /**
   * Locates the Nth element matching given criteria (lazy evaluation)
   *
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @param index       0..N
   * @return SelenideElement
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public static SelenideElement $(String cssSelector, int index) {
    return getSelenideDriver().$(cssSelector, index);
  }

  /**
   * Initialize collection with Elements
   */
  @CheckReturnValue
  @Nonnull
  public static ElementsCollection $$(Collection<? extends WebElement> elements) {
    return getSelenideDriver().$$(elements);
  }

  /**
   * Locates all elements matching given CSS selector (lazy evaluation).
   * <p>
   * Methods returns an ElementsCollection which is a list of WebElement objects that can be iterated,
   * and at the same time is implementation of WebElement interface,
   * meaning that you can call methods .sendKeys(), click() etc. on it.
   *
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @return empty list if element was no found
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public static ElementsCollection $$(String cssSelector) {
    return getSelenideDriver().$$(cssSelector);
  }

  /**
   * Locates all elements matching given XPATH expression (lazy evaluation)
   * <p>
   * Methods returns an ElementsCollection which is a list of WebElement objects that can be iterated,
   * and at the same time is implementation of WebElement interface,
   * meaning that you can call methods .sendKeys(), click() etc. on it.
   *
   * @param xpathExpression any XPATH expression //*[@id='value'] //E[contains(@A, 'value')]
   * @return ElementsCollection which locates elements via XPath
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public static ElementsCollection $$x(String xpathExpression) {
    return getSelenideDriver().$$x(xpathExpression);
  }

  /**
   * Locates all elements matching given CSS selector.
   * <p>
   * Method returns an ElementsCollection which is a list of WebElement objects that can be iterated,
   * and at the same time is implementation of WebElement interface,
   * meaning that you can call methods .sendKeys(), click() etc. on it.
   * </p>
   * @param seleniumSelector any Selenium selector like By.id(), By.name() etc.
   * @return empty list if element was no found
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public static ElementsCollection $$(By seleniumSelector) {
    return getSelenideDriver().$$(seleniumSelector);
  }

  /**
   * Wrap standard Selenium WebElement into SelenideElement
   * to use additional methods like {@link SelenideElement#shouldHave(Condition...)},
   * {@link SelenideElement#selectOption(String, String...)} etc.
   *
   * @param webElement standard Selenium WebElement
   * @return given WebElement wrapped into SelenideElement
   */
  @CheckReturnValue
  @Nonnull
  public static SelenideElement element(WebElement webElement) {
    return getSelenideDriver().$(webElement);
  }

  /**
   * Locates the first element matching given CSS selector
   *
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @return SelenideElement
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public static SelenideElement element(String cssSelector) {
    return getSelenideDriver().$(cssSelector);
  }

  /**
   * Locates the first element matching given CSS selector
   *
   * @param seleniumSelector any Selenium selector like {@link By#id(String)}, {@link By#name(String)} etc.
   * @return SelenideElement
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public static SelenideElement element(By seleniumSelector) {
    return getSelenideDriver().$(seleniumSelector);
  }

  /**
   * Locates the Nth element matching given criteria
   *
   * @param seleniumSelector any Selenium selector like {@link By#id(String)}, {@link By#name(String)} etc.
   * @param index            0..N
   * @return SelenideElement
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public static SelenideElement element(By seleniumSelector, int index) {
    return getSelenideDriver().$(seleniumSelector, index);
  }

  /**
   * Locates the Nth element matching given criteria
   *
   * @param cssSelector any CSS selector like {@code "input[name='first_name']"} or {@code "#messages .new_message"}
   * @param index       0..N
   * @return SelenideElement
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public static SelenideElement element(String cssSelector, int index) {
    return getSelenideDriver().$(cssSelector, index);
  }

  /**
   * Wrap standard Selenium WebElement collection into SelenideElement collection
   * to use additional methods like {@link SelenideElement#shouldHave(Condition...)} etc.
   *
   * @param elements standard Selenium WebElement collection
   * @return given WebElement collection wrapped into SelenideElement collection
   */
  @CheckReturnValue
  @Nonnull
  public static ElementsCollection elements(Collection<? extends WebElement> elements) {
    return getSelenideDriver().$$(elements);
  }

  /**
   * Locates all elements matching given CSS selector.
   * <p>
   * Method returns an ElementsCollection which is a list of WebElement objects that can be iterated,
   * and at the same time is implementation of WebElement interface,
   * meaning that you can call methods {@link WebElement#sendKeys(CharSequence...)}, {@link WebElement#click()} etc. on it.
   *
   * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
   * @return empty list if element was no found
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public static ElementsCollection elements(String cssSelector) {
    return getSelenideDriver().$$(cssSelector);
  }

  /**
   * Locates all elements matching given CSS selector.
   * <p>
   * Method returns an ElementsCollection which is a list of WebElement objects that can be iterated,
   * and at the same time is implementation of WebElement interface,
   * meaning that you can call methods {@link WebElement#sendKeys(CharSequence...)}, {@link WebElement#click()} etc. on it.
   *
   * @param seleniumSelector any Selenium selector like By.id(), By.name() etc.
   * @return empty list if element was no found
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public static ElementsCollection elements(By seleniumSelector) {
    return getSelenideDriver().$$(seleniumSelector);
  }

  /**
   * @see JavascriptExecutor#executeScript(String, java.lang.Object...)
   */
  @Nullable
  public static <T> T executeJavaScript(String jsCode, Object... arguments) {
    return getSelenideDriver().executeJavaScript(jsCode, arguments);
  }

  /**
   * @see JavascriptExecutor#executeAsyncScript(String, java.lang.Object...)
   */
  @Nullable
  public static <T> T executeAsyncJavaScript(String jsCode, Object... arguments) {
    return getSelenideDriver().executeAsyncJavaScript(jsCode, arguments);
  }

  /**
   * Returns selected element in radio group
   *
   * @return null if nothing selected
   */
  @CheckReturnValue
  @Nullable
  public static SelenideElement getSelectedRadio(By radioField) {
    return getSelenideDriver().getSelectedRadio(radioField);
  }

  /**
   * Accept (Click "Yes" or "Ok") in the confirmation dialog (javascript 'alert' or 'confirm').
   *
   * @return actual dialog text
   */
  @Nullable
  public static String confirm() {
    return confirm(ModalOptions.none());
  }

  /**
   * Accept (Click "Yes" or "Ok") in the confirmation dialog (javascript 'alert' or 'confirm').
   *
   * @param expectedDialogText if not null, check that confirmation dialog displays this message (case-sensitive)
   * @return actual dialog text
   * @throws DialogTextMismatch if confirmation message differs from expected message
   */
  @Nullable
  public static String confirm(@Nullable String expectedDialogText) {
    return confirm(withExpectedText(expectedDialogText));
  }

  /**
   * Accept (Click "Yes" or "Ok") in the confirmation dialog (javascript 'alert' or 'confirm').
   *
   * @param options parameters: timeout, expected texts etc.
   * @return actual dialog text
   * @throws DialogTextMismatch if confirmation message differs from expected message
   * @since 6.6.0
   */
  @Nullable
  public static String confirm(ModalOptions options) {
    return getSelenideDriver().modal().confirm(options);
  }

  /**
   * Accept (Click "Yes" or "Ok") in the confirmation dialog (javascript 'prompt').
   *
   * @return actual dialog text
   */
  @Nullable
  public static String prompt() {
    return prompt(ModalOptions.none(), null);
  }

  /**
   * Accept (Click "Yes" or "Ok") in the confirmation dialog (javascript 'prompt').
   *
   * @param inputText if not null, sets value in prompt dialog input
   * @return actual dialog text
   */
  @Nullable
  public static String prompt(@Nullable String inputText) {
    return prompt(ModalOptions.none(), inputText);
  }

  /**
   * Accept (Click "Yes" or "Ok") in the confirmation dialog (javascript 'prompt').
   *
   * @param expectedDialogText if not null, check that confirmation dialog displays this message (case-sensitive)
   * @param inputText          if not null, sets value in prompt dialog input
   * @return actual dialog text
   * @throws DialogTextMismatch if confirmation message differs from expected message
   */
  @Nullable
  public static String prompt(@Nullable String expectedDialogText, @Nullable String inputText) {
    return prompt(withExpectedText(expectedDialogText), inputText);
  }

  /**
   * Accept (Click "Yes" or "Ok") in the confirmation dialog (javascript 'prompt').
   *
   * @param options parameters: timeout, expected texts etc.
   * @param inputText          if not null, sets value in prompt dialog input
   * @return actual dialog text
   * @throws DialogTextMismatch if confirmation message differs from expected message
   * @since 6.6.0
   */
  @Nullable
  public static String prompt(ModalOptions options, @Nullable String inputText) {
    return getSelenideDriver().modal().prompt(options, inputText);
  }

  /**
   * Dismiss (click "No" or "Cancel") in the confirmation dialog (javascript 'alert' or 'confirm').
   *
   * @return actual dialog text
   */
  @Nullable
  public static String dismiss() {
    return dismiss(ModalOptions.none());
  }

  /**
   * Dismiss (click "No" or "Cancel") in the confirmation dialog (javascript 'alert' or 'confirm').
   *
   * @param expectedDialogText if not null, check that confirmation dialog displays this message (case-sensitive)
   * @return actual dialog text
   * @throws DialogTextMismatch if confirmation message differs from expected message
   */
  @Nullable
  public static String dismiss(@Nullable String expectedDialogText) {
    return dismiss(withExpectedText(expectedDialogText));
  }

  /**
   * Dismiss (click "No" or "Cancel") in the confirmation dialog (javascript 'alert' or 'confirm').
   *
   * @param options if not null, check that confirmation dialog displays this message (case-sensitive)
   * @return actual dialog text
   * @throws DialogTextMismatch if confirmation message differs from expected message
   * @since 6.6.0
   */
  @Nullable
  public static String dismiss(ModalOptions options) {
    return getSelenideDriver().modal().dismiss(options);
  }

  /**
   * Switch to window/tab/frame/parentFrame/innerFrame/alert.
   * Allows switching to window by title, index, name etc.
   * <p>
   * Similar to {@link org.openqa.selenium.WebDriver#switchTo()}, but all methods wait until frame/window/alert
   * appears if it's not visible yet (like other Selenide methods).
   *
   * @return SelenideTargetLocator
   */
  @Nonnull
  @CheckReturnValue
  public static SelenideTargetLocator switchTo() {
    return getSelenideDriver().driver().switchTo();
  }

  /**
   * @return the element that currently is focused, or null if none of elements if focused
   */
  @CheckReturnValue
  public static SelenideElement getFocusedElement() {
    return getSelenideDriver().getFocusedElement();
  }

  /**
   * Returns selected text or empty string if no text is selected.
   *
   * @return selected text
   * @since 6.11.0
   */
  @CheckReturnValue
  @Nonnull
  public static String getSelectedText() {
    return getSelenideDriver().getSelectedText();
  }

  /**
   * Copy selected text or empty string if no text is selected to clipboard.
   *
   * @return the copied text
   *
   * @see #clipboard()
   * @see Clipboard
   * @since 6.11.0
   */
  public static String copy() {
    return getSelenideDriver().copy();
  }

  /**
   * Create a Page Object instance
   */
  @CheckReturnValue
  @Nonnull
  public static <PageObjectClass> PageObjectClass page(Class<PageObjectClass> pageObjectClass) {
    return getSelenideDriver().page(pageObjectClass);
  }

  /**
   * Create a Page Object instance
   * @since 6.8.0
   * @param reified Don't pass any values here. It's Java Magic :)
   */
  @CheckReturnValue
  @Nonnull
  public static <PageObjectClass> PageObjectClass page(PageObjectClass... reified) {
    return getSelenideDriver().page(reified);
  }

  /**
   * Initialize a given Page Object instance
   */
  @CheckReturnValue
  @Nonnull
  public static <PageObjectClass, T extends PageObjectClass> PageObjectClass page(T pageObject) {
    return getSelenideDriver().page(pageObject);
  }

  /**
   * Create a org.openqa.selenium.support.ui.FluentWait instance with Selenide timeout/polling.
   * <p>
   * Sample usage:
   * {@code
   * Wait().until(invisibilityOfElementLocated(By.id("magic-id")));
   * }
   *
   * @return instance of org.openqa.selenium.support.ui.FluentWait
   */
  @CheckReturnValue
  @Nonnull
  public static SelenideWait Wait() {
    return getSelenideDriver().Wait();
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
  @CheckReturnValue
  @Nonnull
  public static Actions actions() {
    return getSelenideDriver().driver().actions();
  }

  /**
   * Zoom current page (in or out).
   *
   * @param factor e.g. 1.1 or 2.0 or 0.5
   */
  public static void zoom(double factor) {
    getSelenideDriver().zoom(factor);
  }

  /**
   * Same as {@link Selenide#getWebDriverLogs(String, Level)}
   */
  @CheckReturnValue
  @Nonnull
  public static List<String> getWebDriverLogs(String logType) {
    return getSelenideDriver().getWebDriverLogs().logs(logType);
  }

  /**
   * Getting and filtering of the WebDriver logs for specified LogType by specified logging level
   * <br>
   * For example to get WebDriver Browser's console output (including JS info, warnings, errors, etc. messages)
   * you can use:
   * <br>
   * <pre>
   *   {@code
   *     for(String logEntry : getWebDriverLogs(LogType.BROWSER, Level.ALL)) {
   *       Reporter.log(logEntry + "<br>");
   *     }
   *   }
   * </pre>
   * <br>
   * Be aware that currently "manage().logs()" is in the Beta stage, but it is beta-then-nothing :)
   * <br>
   * List of the unsupported browsers and issues:
   * <br>
   * http://bit.ly/RZcmrM
   * <br>
   * http://bit.ly/1nZTaqu
   * <br>
   *
   * @param logType  WebDriver supported log types
   * @param logLevel logging level that will be used to control logging output
   * @return list of log entries
   * @see LogType
   * @see Level
   */
  @Nonnull
  @CheckReturnValue
  public static List<String> getWebDriverLogs(String logType, Level logLevel) {
    return getSelenideDriver().getWebDriverLogs().logs(logType, logLevel);
  }

  /**
   * Clear browser cookies.
   * <p>
   * It can be useful e.g. if you are trying to avoid restarting browser between tests
   */
  public static void clearBrowserCookies() {
    getSelenideDriver().clearCookies();
  }

  /**
   * Clear browser local storage.
   * <p>
   * In case if you need to be sure that browser's localStorage is empty
   */
  public static void clearBrowserLocalStorage() {
    getSelenideDriver().clearBrowserLocalStorage();
  }

  /**
   * Get current user agent from browser session
   *
   * @return browser user agent
   */
  @Nonnull
  @CheckReturnValue
  public static String getUserAgent() {
    return getSelenideDriver().driver().getUserAgent();
  }

  /**
   * Return true if bottom of the page is reached
   * <p>
   * Useful if you need to scroll down by x pixels unknown number of times.
   */
  @CheckReturnValue
  public static boolean atBottom() {
    return getSelenideDriver().atBottom();
  }

  /**
   * NB! URL must be properly encoded.
   * E.g. instead of "/files/ж.txt", it should be "/files/%D0%B6.txt"
   *
   * @see #download(String, long)
   * Download fails if default timeout (Configuration.timeout) is exceeded
   */
  @Nonnull
  @CheckReturnValue
  public static File download(String url) throws IOException, URISyntaxException {
    return getSelenideDriver().download(url);
  }

  /**
   * @see #download(String)
   */
  @Nonnull
  @CheckReturnValue
  public static File download(URI url) throws IOException {
    return getSelenideDriver().download(url);
  }

  /**
   * @see #download(String, long)
   */
  @Nonnull
  @CheckReturnValue
  public static File download(URI url, long timeoutMs) throws IOException {
    return getSelenideDriver().download(url, timeoutMs);
  }

  /**
   * Download file using a direct link.
   * This method download file like it would be done in currently opened browser:
   * it adds all cookies and "User-Agent" header to the downloading request.
   * <p>
   * Download fails if specified timeout is exceeded
   *
   * @param url       either relative or absolute url
   *                  NB! URL must be properly encoded.
   *                  E.g. instead of "/files/ж.txt", it should be "/files/%D0%B6.txt"
   * @param timeoutMs specific timeout in ms
   * @return downloaded File in folder `Configuration.reportsFolder`
   * @throws IOException        if failed to download file
   * @throws URISyntaxException if given url has invalid syntax
   */
  @Nonnull
  @CheckReturnValue
  public static File download(String url, long timeoutMs) throws IOException, URISyntaxException {
    return getSelenideDriver().download(new URI(url), timeoutMs);
  }

  /**
   * Access browser's local storage.
   * Allows setting, getting, removing items as well as getting the size and clear the storage.
   *
   * @return LocalStorage
   * @since 5.15.0
   */
  @Nonnull
  @CheckReturnValue
  public static LocalStorage localStorage() {
    return getSelenideDriver().getLocalStorage();
  }

  /**
   * Access browser's session storage.
   * Allows setting, getting, removing items as well as getting the size, check for contains item and clear the storage.
   *
   * @return sessionStorage
   * @since 5.18.1
   */
  @Nonnull
  @CheckReturnValue
  public static SessionStorage sessionStorage() {
    return getSelenideDriver().getSessionStorage();
  }

  /**
   * Provide access to system clipboard, allows get and set String content.
   * Default implementation acts via {@link java.awt.Toolkit} and supports only local runs.
   * <p>
   * Remote runs support can be implemented via plugins.
   * Plugin for Selenoid supports clipboard since v1.1.0.
   * @see <a href="https://github.com/selenide/selenide/tree/main/modules/selenoid">selenide-selenoid</a>
   * <p>
   * Pay attention that Clipboard is shared resource for instance where tests runs
   * and keep in mind while developing test suite with multiple tests for clipboard.
   *
   * @return Clipboard
   * @since 5.20.0
   */
  @Nonnull
  @CheckReturnValue
  public static Clipboard clipboard() {
    return getSelenideDriver().getClipboard();
  }

  /**
   * Get current browser session Id
   *
   * @return SessionId
   */
  @Nonnull
  @CheckReturnValue
  public static SessionId sessionId() {
    return getSelenideDriver().getSessionId();
  }
}
