package com.codeborne.selenide;

import com.codeborne.selenide.impl.WebDriverThreadLocalContainer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import static com.codeborne.selenide.Configuration.browser;

public class WebDriverRunner {
  public static WebDriverThreadLocalContainer webdriverContainer = new WebDriverThreadLocalContainer();

  public static final String CHROME = "chrome";
  public static final String INTERNET_EXPLORER = "ie";
  public static final String FIREFOX = "firefox";

  /**
   * To use HtmlUnitDriver, you need to include extra dependency to your project:
   * <dependency org="org.seleniumhq.selenium" name="selenium-htmlunit-driver" rev="2.35.0" conf="test->default"/>
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
   * To use PhantomJS, you need to include extra dependency to your project:
   * &lt;dependency org="com.github.detro.ghostdriver" name="phantomjsdriver" rev="1.+" conf="test-&gt;default"/&gt;
   */
  public static final String PHANTOMJS = "phantomjs";

  /**
   * To use OperaDriver, you need to include extra dependency to your project:
   * &lt;dependency org="com.opera" name="operadriver" rev="1.5" conf="test-&gt;default"/&gt;
   */
  public static final String OPERA = "opera";

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
   * <p/>
   * <p/>
   * P.S. Alternatively, you can run tests with system property
   * <pre>  -Dbrowser=com.my.WebDriverFactory</pre>
   *
   * which should implement interface com.codeborne.selenide.WebDriverProvider
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
   * Close the browser if it's open
   */
  public static void closeWebDriver() {
    webdriverContainer.closeWebDriver();
  }

  /**
   * Is Selenide configured to use Firefox browser
   */
  public static boolean isFirefox() {
    return FIREFOX.equalsIgnoreCase(browser);
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
  public static boolean ie() {
    return INTERNET_EXPLORER.equalsIgnoreCase(browser);
  }

  /**
   * Is Selenide configured to use headless browser (HtmlUnit or PhantomJS)
   */
  public static boolean isHeadless() {
    return htmlUnit() || phantomjs();
  }

  /**
   * Is Selenide configured to use HtmlUnit browser
   */
  public static boolean htmlUnit() {
    return browser != null && browser.startsWith(HTMLUNIT);
  }

  /**
   * Is Selenide configured to use PhantomJS browser
   */
  public static boolean phantomjs() {
    return PHANTOMJS.equalsIgnoreCase(browser);
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
   * @deprecated Use com.codeborne.selenide.Screenshots#takeScreenShot(java.lang.String, java.lang.String)
   */
  @Deprecated
  public static String takeScreenShot(String className, String methodName) {
    return Screenshots.takeScreenShot(className, methodName);
  }

  /**
   * @deprecated Use com.codeborne.selenide.Screenshots#takeScreenShot(java.lang.String)
   */
  @Deprecated
  public static String takeScreenShot(String fileName) {
    return Screenshots.takeScreenShot(fileName);
  }
}
