package com.codeborne.selenide;

import com.codeborne.selenide.impl.WebDriverContainer;
import com.codeborne.selenide.impl.WebDriverThreadLocalContainer;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import static com.codeborne.selenide.Configuration.browser;

public class WebDriverRunner {
  public static WebDriverContainer webdriverContainer = new WebDriverThreadLocalContainer();

  public static final String CHROME = "chrome";
  public static final String INTERNET_EXPLORER = "ie";
  public static final String FIREFOX = "firefox";
  /**
   * Marinonette Driver for Firefox is the same as Gecko Driver
   */
  public static final String MARIONETTE = "marionette";
  /**
   * Marinonette Driver for Firefox is the same as Gecko Driver
   */
  public static final String GECKO = "gecko";

  /**
   * To use Safari webdriver, you need to include extra dependency to your project:
   * &lt;dependency org="org.seleniumhq.selenium" name="selenium-safari-driver" rev="2.+" conf="test-&gt;default"/&gt;
   */
  public static final String SAFARI = "safari";

  /**
   * To use HtmlUnitDriver, you need to include extra dependency to your project:
   * <dependency org="org.seleniumhq.selenium" name="selenium-htmlunit-driver" rev="2.+" conf="test->default"/>
   *
   * It's also possible to run HtmlUnit driver emulating different browsers:
   * <p>
   * java -Dbrowser=htmlunit:firefox
   * </p>
   * <p>
   * java -Dbrowser=htmlunit:chrome
   * </p>
   * <p>
   * java -Dbrowser=htmlunit:internet explorer   (default)
   * </p>
   * etc.
   */
  public static final String HTMLUNIT = "htmlunit";

  /**
   * To use PhantomJS, you need to set system property: -Dselenide.browser=phantomjs
   */
  public static final String PHANTOMJS = "phantomjs";

  /**
   * To use OperaDriver, you need to include extra dependency to your project:
   * &lt;dependency org="com.opera" name="operadriver" rev="1.5" conf="test-&gt;default"/&gt;
   */
  public static final String OPERA = "opera";
  
  /**
   * To use JbrowserDriver, you need to include extra dependency to your project:
   * <dependency org="com.machinepublishers" name="jbrowserdriver" rev="[0.13.0, 2.0)" conf="test-&gt;default"/&gt;
   * 
   * Note: You need minimum of Java 8.
   */
  public static final String JBROWSER = "jbrowser";

  /**
   * Use this method BEFORE opening a browser to add custom event listeners to webdriver.
   * @param listener your listener of webdriver events
   */
  public static void addListener(WebDriverEventListener listener) {
    webdriverContainer.addListener(listener);
  }

  /**
   * Tell Selenide use your provided WebDriver instance.
   * Use it if you need a custom logic for creating WebDriver.
   *
   * It's recommended not to use implicit wait with this driver, because Selenide handles timing issues explicitly.
   *
   * <p/>
   * <p/>
   *
   * NB! Be sure to call this method before calling <code>open(url)</code>.
   * Otherwise Selenide will create its own WebDriver instance and would not close it.
   *
   * <p>
   * NB! When using your custom webdriver, you are responsible for closing it.
   *     Selenide will not take care of it.
   * </p>
   *
   * <p>
   * NB! Webdriver instance should be created and used in the same thread.
   *     A typical error is to create webdriver instance in one thread and use it in another.
   *     Selenide does not support it.
   *     If you really need using multiple threads, please use #com.codeborne.selenide.WebDriverProvider
   * </p>
   *
   * <p>
   * P.S. Alternatively, you can run tests with system property
   * <pre>  -Dbrowser=com.my.WebDriverFactory</pre>
   *
   * which should implement interface #com.codeborne.selenide.WebDriverProvider
   * </p>
   */
  public static void setWebDriver(WebDriver webDriver) {
    webdriverContainer.setWebDriver(webDriver);
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
   *
   * @param webProxy
   */
  public static void setProxy(Proxy webProxy) {
    webdriverContainer.setProxy(webProxy);
  }

  /**
   * Get the underlying instance of Selenium WebDriver, and assert that it's still alive.
   * @return new instance of WebDriver if the previous one has been closed meanwhile.
   */
  public static WebDriver getAndCheckWebDriver() {
    return webdriverContainer.getAndCheckWebDriver();
  }

  /**
   * Close the browser if it's open
   */
  public static void closeWebDriver() {
    webdriverContainer.closeWebDriver();
  }

  /**
   * @return true iff instance of Selenium WebDriver is started in current thread
   */
  public static boolean hasWebDriverStarted() {
    return webdriverContainer.hasWebDriverStarted();
  }

  /**
   * Is Selenide configured to use Firefox browser
   */
  public static boolean isFirefox() {
    return FIREFOX.equalsIgnoreCase(browser);
  }

  /**
   * Is Selenide configured to use Marionette (Gecko) driver
   */
  public static boolean isMarionette() {
    return MARIONETTE.equalsIgnoreCase(browser) || GECKO.equalsIgnoreCase(browser);
  }

  /**
   * Is Selenide configured to use Chrome browser
   */
  public static boolean isChrome() {
    return CHROME.equalsIgnoreCase(browser);
  }

  /**
   * Is Selenide configured to use Internet Explorer browser
   */
  public static boolean isIE() {
    return INTERNET_EXPLORER.equalsIgnoreCase(browser);
  }

  /**
   * Is Selenide configured to user Safari browser
   */
  public static boolean isSafari() {
    return SAFARI.equalsIgnoreCase(browser);
  }

  /**
   * Is Selenide configured to use headless browser (HtmlUnit or PhantomJS)
   */
  public static boolean isHeadless() {
    return isHtmlUnit() || isPhantomjs();
  }

  /**
   * Does this browser support "alert" and "confirm" dialogs.
   */
  public static boolean supportsModalDialogs() {
    return !isHeadless() && !isSafari();
  }

  /**
   * Does this browser support javascript
   */
  public static boolean supportsJavascript() {
    return hasWebDriverStarted() && getWebDriver() instanceof JavascriptExecutor;
  }

  /**
   * Is Selenide configured to use HtmlUnit browser
   */
  public static boolean isHtmlUnit() {
    return browser != null && browser.startsWith(HTMLUNIT);
  }

  /**
   * Is Selenide configured to use PhantomJS browser
   */
  public static boolean isPhantomjs() {
    return PHANTOMJS.equalsIgnoreCase(browser);
  }

  /**
   * Is Selenide configured to use Opera browser
   */
  public static boolean isOpera() {
    return OPERA.equalsIgnoreCase(browser);
  }
  
  /**
   * Is Selenide configured to use JBrowser browser
   */
  public static boolean isJBrowser() {
    return JBROWSER.equalsIgnoreCase(browser);
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
  public static String source() {
    return webdriverContainer.getPageSource();
  }

  /**
   * @return the URL of current page
   */
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
