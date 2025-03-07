package com.codeborne.selenide;

import com.codeborne.selenide.impl.WebDriverContainer;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverListener;

import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.headless;
import static com.codeborne.selenide.impl.Plugins.inject;

/**
 * A static facade for accessing WebDriver instance for current threads
 */
public class WebDriverRunner {
  private static final WebDriverContainer webdriverContainer = inject(WebDriverContainer.class);
  private static final ThreadLocalSelenideDriver staticSelenideDriver = new ThreadLocalSelenideDriver();

  /**
   * Use this method BEFORE opening a browser to add custom event listeners to webdriver.
   *
   * @param listener your listener of webdriver events
   */
  public static void addListener(WebDriverListener listener) {
    webdriverContainer.addListener(listener);
  }

  public static void removeListener(WebDriverListener listener) {
    webdriverContainer.removeListener(listener);
  }

  /**
   * Tell Selenide use your provided WebDriver instance.
   * Use it if you need a custom logic for creating WebDriver.
   * <p>
   * It's recommended not to use implicit wait with this driver, because Selenide handles timing issues explicitly.
   *
   * <br>
   * <p>
   * NB! Be sure to call this method before calling {@code open(url)}.
   * Otherwise, Selenide will create its own WebDriver instance and would not close it.
   *
   * <p>
   * NB! When using your custom webdriver, you are responsible for closing it.
   * Selenide will not take care of it.
   * </p>
   *
   * <p>
   * NB! Webdriver instance should be created and used in the same thread.
   * A typical error is to create webdriver instance in one thread and use it in another.
   * Selenide does not support it.
   * If you really need using multiple threads, please use #com.codeborne.selenide.WebDriverProvider
   * </p>
   *
   * <p>
   * P.S. Alternatively, you can run tests with system property
   * <pre>  -Dbrowser=com.my.WebDriverFactory</pre>
   * <p>
   * which should implement interface #com.codeborne.selenide.WebDriverProvider
   * </p>
   */
  public static void setWebDriver(WebDriver webDriver) {
    webdriverContainer.setWebDriver(webDriver);
  }

  public static void setWebDriver(WebDriver webDriver, @Nullable SelenideProxyServer selenideProxy) {
    webdriverContainer.setWebDriver(webDriver, selenideProxy);
  }

  public static void setWebDriver(WebDriver webDriver,
                                  @Nullable SelenideProxyServer selenideProxy,
                                  DownloadsFolder browserDownloadsFolder) {
    webdriverContainer.setWebDriver(webDriver, selenideProxy, browserDownloadsFolder);
  }

  /**
   * Get the underlying instance of Selenium WebDriver.
   * This can be used for any operations directly with WebDriver.
   */
  public static WebDriver getWebDriver() {
    return webdriverContainer.getWebDriver();
  }

  /**
   * Sets Selenium Proxy instance
   */
  public static void setProxy(@Nullable Proxy webProxy) {
    webdriverContainer.setProxy(webProxy);
  }

  /**
   * Get the underlying instance of Selenium WebDriver, and assert that it's still alive.
   *
   * @return new instance of WebDriver if the previous one has been closed meanwhile.
   */
  public static WebDriver getAndCheckWebDriver() {
    return webdriverContainer.getAndCheckWebDriver();
  }

  /**
   * Get selenide proxy. It's activated only if Configuration.proxyEnabled == true
   */
  public static SelenideProxyServer getSelenideProxy() {
    return webdriverContainer.getProxyServer();
  }

  static ThreadLocalSelenideDriver getSelenideDriver() {
    return staticSelenideDriver;
  }

  public static Driver driver() {
    return getSelenideDriver().driver();
  }

  @Nullable
  public static DownloadsFolder getBrowserDownloadsFolder() {
    return webdriverContainer.getBrowserDownloadsFolder();
  }

  /**
   * Close the current window, quitting the browser if it's the last window currently open.
   *
   * @see WebDriver#close()
   */
  public static void closeWindow() {
    webdriverContainer.closeWindow();
  }

  /**
   * <p>Close the browser if it's open.</p>
   * <br>
   * <p>NB! Method quits this driver, closing every associated window.</p>
   *
   * @see WebDriver#quit()
   * @deprecated Use method {@link Selenide#closeWebDriver()} instead
   */
  @Deprecated
  public static void closeWebDriver() {
    webdriverContainer.closeWebDriver();
  }

  /**
   * @return true if instance of Selenium WebDriver is started in current thread
   */
  public static boolean hasWebDriverStarted() {
    return webdriverContainer.hasWebDriverStarted();
  }

  static void using(WebDriver driver, Runnable lambda) {
    webdriverContainer.using(driver, null, null, lambda);
  }

  static void using(WebDriver driver, SelenideProxyServer proxy, Runnable lambda) {
    webdriverContainer.using(driver, proxy, null, lambda);
  }

  static void inNewBrowser(Runnable lambda) {
    webdriverContainer.inNewBrowser(lambda);
  }

  static void inNewBrowser(Config config, Runnable lambda) {
    webdriverContainer.inNewBrowser(config, lambda);
  }

  private static Browser browser() {
    return new Browser(browser, headless);
  }

  /**
   * Is Selenide configured to use Firefox browser
   */
  public static boolean isFirefox() {
    return browser().isFirefox();
  }

  /**
   * Is Selenide configured to use Chrome browser
   */
  public static boolean isChrome() {
    return browser().isChrome();
  }

  /**
   * Is Selenide configured to use Internet Explorer browser
   */
  public static boolean isIE() {
    return browser().isIE();
  }

  /**
   * Is Selenide configured to use Microsoft EDGE browser
   */
  public static boolean isEdge() {
    return browser().isEdge();
  }

  /**
   * Is Selenide configured to use headless browser
   */
  public static boolean isHeadless() {
    return browser().isHeadless();
  }

  /**
   * Does this browser support javascript
   */
  public static boolean supportsJavascript() {
    return driver().supportsJavascript();
  }

  /**
   * Delete all the browser cookies
   */
  public static void clearBrowserCache() {
    webdriverContainer.clearBrowserCache();
  }

  /**
   * @return the source (HTML) of current page
   */
  @Nullable
  public static String source() {
    return webdriverContainer.getPageSource();
  }

  /**
   * @return the URL of current page
   */
  @Nullable
  public static String url() {
    return webdriverContainer.getCurrentUrl();
  }

  /**
   * @return the URL of current frame
   */
  public static String currentFrameUrl() {
    return webdriverContainer.getCurrentFrameUrl();
  }
}
