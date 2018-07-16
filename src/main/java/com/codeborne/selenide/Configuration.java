package com.codeborne.selenide;

import java.util.logging.Logger;
import org.openqa.selenium.remote.DesiredCapabilities;

import static com.codeborne.selenide.Configuration.AssertionMode.STRICT;
import static com.codeborne.selenide.Configuration.SelectorMode.CSS;

public class Configuration {
  private static final Logger LOG = Logger.getLogger(Configuration.class.getName());

  private static final SelenideConfig SELENIDE_CONFIG = new SelenideConfig(){};

  /**
   * Base url for open() function calls
   * Can be configured either programmatically or by system property "-Dselenide.baseUrl=http://myhost".
   * Default value: http://localhost:8080
   */
  public static String baseUrl = SELENIDE_CONFIG.baseUrl();

  /**
   * Timeout in milliseconds for a collection to get completely loaded
   * Conditions will be checked at this point at latest, even if they are still loading
   * Can be configured either programmatically or by system property "-Dselenide.collectionsTimeout=10000"
   * Default value: 6000 (milliseconds)
   */
  public static long collectionsTimeout = SELENIDE_CONFIG.collectionsTimeout();

  /**
   * Timeout in milliseconds to fail the test, if conditions still not met
   * Can be configured either programmatically or by system property "-Dselenide.timeout=10000"
   * Default value: 4000 (milliseconds)
   */
  public static long timeout = SELENIDE_CONFIG.timeout();

  /**
   * Interval in milliseconds, when checking if a single element is appeared
   * Can be configured either programmatically or by system property "-Dselenide.pollingInterval=50"
   * Default value: 100 (milliseconds)
   */
  public static long pollingInterval = SELENIDE_CONFIG.pollingInterval();

  /**
   * Interval in milliseconds, when checking if a new collection elements appeared
   * Can be configured either programmatically or by system property "-Dselenide.collectionsPollingInterval=150"
   * Default value: 200 (milliseconds)
   */
  public static long collectionsPollingInterval = SELENIDE_CONFIG.collectionsPollingInterval();

  /**
   * If holdBrowserOpen is true, browser window stays open after running tests. It may be useful for debugging.
   * Can be configured either programmatically or by system property "-Dselenide.holdBrowserOpen=true".
   * <p/>
   * Default value: false.
   */
  public static boolean holdBrowserOpen = SELENIDE_CONFIG.holdBrowserOpen();

  /**
   * Should Selenide re-spawn browser if it's disappeared (hangs, broken, unexpectedly closed).
   * <p>
   * Can be configured either programmatically or by system property "-Dselenide.reopenBrowserOnFail=false".
   * <p>
   * Default value: true
   * Set this property to false if you want to disable automatic re-spawning the browser.
   */
  public static boolean reopenBrowserOnFail = SELENIDE_CONFIG.reopenBrowserOnFail();

  /**
   * Timeout (in milliseconds) for opening (creating) a browser (webdriver).
   * <p/>
   * Can be configured either programmatically or by system property "-Dselenide.openBrowserTimeout=10000"
   * Default value: 15000 (milliseconds)
   */
  public static long openBrowserTimeoutMs = SELENIDE_CONFIG.openBrowserTimeoutMs();

  /**
   * Timeout (in milliseconds) for closing/killing browser.
   * <p/>
   * Sometimes we have problems with calling driver.close() or driver.quit() method, and test always is suspended too long.
   * <p/>
   * Can be configured either programmatically or by system property "-Dselenide.closeBrowserTimeout=10000"
   * Default value: 5000 (milliseconds)
   */
  public static long closeBrowserTimeoutMs = SELENIDE_CONFIG.closeBrowserTimeoutMs();

  /**
   * Which browser to use.
   * Can be configured either programmatically or by system property "-Dselenide.browser=ie" or "-Dbrowser=ie".
   * Supported values: "chrome", "firefox", "legacy_firefox", "ie", "htmlunit", "phantomjs", "opera", "safari", "edge", "jbrowser"
   * <p/>
   * Default value: "firefox"
   */
  public static String browser = SELENIDE_CONFIG.browser();

  /**
   * Which browser version to use (for Internet Explorer).
   * Can be configured either programmatically or by system property "-Dselenide.browserVersion=8" or "-Dbrowser.version=8".
   * <p/>
   * Default value: none
   */
  public static String browserVersion = SELENIDE_CONFIG.browserVersion();

  /**
   * URL of remote web driver (in case of using Selenium Grid).
   * Can be configured either programmatically or by system property "-Dremote=http://localhost:5678/wd/hub".
   *
   * Default value: null (Grid is not used).
   */
  public static String remote = SELENIDE_CONFIG.remote();

  /**
   * The browser window size.
   * Can be configured either programmatically or by system property "-Dselenide.browserSize=1024x768".
   *
   * Default value: none (browser size will not be set explicitly)
   */
  public static String browserSize = SELENIDE_CONFIG.browserSize();

  /**
   * The browser window position on screen.
   * Can be configured either programmatically or by system property "-Dselenide.browserPosition=10x10".
   *
   * Default value: none (browser window position will not be set explicitly)
   */
  public static String browserPosition = SELENIDE_CONFIG.browserPosition();

  /**
   * The browser window is maximized when started.
   * Can be configured either programmatically or by system property "-Dselenide.startMaximized=true".
   * <p>
   * Default value: true
   */
  public static boolean startMaximized = SELENIDE_CONFIG.startMaximized();

  /**
   * @deprecated this options allowed only a single switch.
   *   Please use instead more generic -Dchromeoptions.args=<comma-separated list of switches>
   *   <p>
   *   or use -Dchromeoptions.prefs=<comma-separated dictionary of key=value>
   *   <p>
   *
   * Value of "chrome.switches" parameter (in case of using Chrome driver).
   * Can be configured either programmatically or by system property,
   * i.e. "-Dselenide.chrome.switches=--disable-popup-blocking".
   *
   * Default value: none
   */
  @Deprecated
  public static String chromeSwitches = SELENIDE_CONFIG.chromeSwitches();

  /**
   * Browser capabilities.
   * Warning: this capabilities will override capabilities were set by system properties.
   * <p/>
   * Default value: null
   */
  public static DesiredCapabilities browserCapabilities;
  /**
   * Should webdriver wait until page is completely loaded.
   * Possible values: "none", "normal" and "eager".
   *
   * Can be configured either programmatically or by system property "-Dselenide.pageLoadStrategy=eager".
   * Default value: "normal".
   *
   *  - `normal`: return after the load event fires on the new page (it's default in Selenium webdriver);
   *  - `eager`: return after DOMContentLoaded fires;
   *  - `none`: return immediately
   *
   *  In some cases `eager` can bring performance boosts for the slow tests.
   *  Though, we left default value `normal` because we afraid to break users' existing tests.
   *
   * See https://w3c.github.io/webdriver/webdriver-spec.html#dfn-page-loading-strategy
   * @since 3.5
   */
  public static String pageLoadStrategy = SELENIDE_CONFIG.pageLoadStrategy();

  /**
   * ATTENTION! Automatic WebDriver waiting after click isn't working in case of using this feature.
   * Use clicking via JavaScript instead common element clicking.
   * This solution may be helpful for testing in Internet Explorer.
   * Can be configured either programmatically or by system property "-Dselenide.clickViaJs=true".
   * Default value: false
   */
  public static boolean clickViaJs = SELENIDE_CONFIG.clickViaJs();

  /**
   * Defines if Selenide tries to capture JS errors
   * Can be configured either programmatically or by system property "-Dselenide.captureJavascriptErrors=false".
   *
   * Default value: true
   */
  public static boolean captureJavascriptErrors = SELENIDE_CONFIG.captureJavascriptErrors();

  /**
   * Defines if Selenide takes screenshots on failing tests.
   * Can be configured either programmatically or by system property "-Dselenide.screenshots=false".
   *
   * Default value: true
   */
  public static boolean screenshots = SELENIDE_CONFIG.screenshots();

  /**
   * Defines if Selenide saves page source on failing tests.
   * Can be configured either programmatically or by system property "-Dselenide.savePageSource=false".
   * Default value: true
   */
  public static boolean savePageSource = SELENIDE_CONFIG.savePageSource();

  /**
   * Folder to store screenshots to.
   * Can be configured either programmatically or by system property "-Dselenide.reportsFolder=test-result/reports".
   *
   * Default value: "build/reports/tests" (this is default for Gradle projects)
   */
  public static String reportsFolder = SELENIDE_CONFIG.reportsFolder();

  /**
   * Optional: URL of CI server where reports are published to.
   * In case of Jenkins, it is "BUILD_URL/artifact" by default.
   *
   * Can be configured either programmatically or by system property "-Dselenide.reportsUrl=http://jenkins-host/reports".
   *
   * If it's given, names of screenshots are printed as
   * "http://ci.mycompany.com/job/my-job/446/artifact/build/reports/tests/my_test.png" - it's useful to analyze test
   * failures in CI server.
   */
  public static String reportsUrl = getReportsUrl();

  public static void load(SelenideConfig config){
    browser = config.browser();
    browserSize = config.browserSize();
    browserCapabilities = config.browserCapabilities();
    browserVersion = config.browserVersion();
    browserBinary = config.browserBinary();
    baseUrl = config.baseUrl();
    browserPosition = config.browserPosition();
    headless = config.headless();
    timeout = config.timeout();
    collectionsTimeout = config.collectionsTimeout();
    pollingInterval = config.pollingInterval();
    collectionsPollingInterval = config.collectionsPollingInterval();
    holdBrowserOpen = config.holdBrowserOpen();
    reopenBrowserOnFail= config.reopenBrowserOnFail();
    openBrowserTimeoutMs = config.openBrowserTimeoutMs();
    remote = config.remote();
    startMaximized = config.startMaximized();
    reportsFolder = config.reportsFolder();
    chromeSwitches = config.chromeSwitches();
    pageLoadStrategy = config.pageLoadStrategy();
    clickViaJs = config.clickViaJs();
    captureJavascriptErrors = config.captureJavascriptErrors();
    screenshots = config.screenshots();
    savePageSource = config.savePageSource();
    reportsUrl = config.reportsUrl();
    dismissModalDialogs = config.dismissModalDialogs();
    fastSetValue = config.fastSetValue();
    versatileSetValue = config.versatileSetValue();
    setValueChangeEvent = config.setValueChangeEvent();
    fileDownload = config.fileDownloadMode();
    driverManagerEnabled = config.driverManagerEnabled();
  }

  static String getReportsUrl() {
    String reportsUrl = System.getProperty("selenide.reportsUrl");
    if (isEmpty(reportsUrl)) {
      reportsUrl = getJenkinsReportsUrl();
      if (isEmpty(reportsUrl)) {
        LOG.config("Variable selenide.reportsUrl not found");
      }
    } else {
      LOG.config("Using variable selenide.reportsUrl=" + reportsUrl);
    }
    return reportsUrl;
  }

  private static boolean isEmpty(String s) {
    return s == null || s.trim().isEmpty();
  }

  private static String getJenkinsReportsUrl() {
    String build_url = System.getProperty("BUILD_URL");
    if (!isEmpty(build_url)) {
      LOG.config("Using Jenkins BUILD_URL: " + build_url);
      return build_url + "artifact/";
    }
    else {
      LOG.config("No BUILD_URL variable found. It's not Jenkins.");
      return null;
    }
  }

  /**
   * Mock "alert" and "confirm" javascript dialogs.
   * Can be configured either programmatically or by system property "-Dselenide.dismissModalDialogs=true".
   *
   * Default value: false
   *        (true for headless browsers like HtmlUnit and PhantomJS because they do not support alert/confirm anyway)
   */
  public static boolean dismissModalDialogs = SELENIDE_CONFIG.dismissModalDialogs();

  /**
   * If set to true, sets value by javascript instead of using Selenium built-in "sendKey" function
   * (that is quite slow because it sends every character separately).
   *
   * Tested on Codeborne projects - works well, speed up ~30%.
   * Some people reported 150% speedup (because sending characters one-by-one was especially
   * slow via network to Selenium Grid on cloud).
   *
   * https://github.com/codeborne/selenide/issues/135
   * Can be configured either programmatically or by system property "-Dselenide.fastSetValue=true".
   * Default value: false
   */
  public static boolean fastSetValue = SELENIDE_CONFIG.fastSetValue();

  /**
   * If set to true, 'setValue' and 'val' methods of SelenideElement can work as 'selectOptionByValue', 'selectRadio'
   * depending on the real control type, defined by element's tag.
   *
   * Will decrease performance of setValue, make it slower, but will also make tests implementation more "business oriented".
   * With this property being set to true, tests may no longer be dependent on actual control implementation in html and
   * be more abstract.
   *
   * https://github.com/codeborne/selenide/issues/508
   * Can be configured either programmatically or by system property "-Dselenide.versatileSetValue=true".
   * Default value: false
   */
  public static boolean versatileSetValue = SELENIDE_CONFIG.versatileSetValue();

  /**
   * If set to true, 'setValue' and 'val' methods of SelenideElement trigger changeEvent after main manipulations.
   *
   * Firing change event is not natural and could lead to unpredictable results. Browser fires this event automatically
   * according to web driver actions. Recommended behaviour is to disable this option.
   * Make its true by default for backward compatibility.
   *
   * Can be configured either programmatically or by system property "-Dselenide.setValueChangeEvent=true".
   * Default value: true
   */
  public static boolean setValueChangeEvent = SELENIDE_CONFIG.setValueChangeEvent();

  /**
   * Choose how Selenide should retrieve web elements: using default CSS or Sizzle (CSS3)
   */
  public static SelectorMode selectorMode = CSS;

  public enum SelectorMode {
    /**
     * Default Selenium behavior
     */
    CSS,

    /**
     * Use Sizzle for CSS selectors.
     * It allows powerful CSS3 selectors - ":input", ":not", ":nth", ":first", ":last", ":contains('text')"
     *
     * For other selectors (XPath, ID etc.) uses default Selenium mechanism.
     */
    Sizzle
  }


  /**
   * Assertion modes available
   */
  public enum AssertionMode {
    /**
     * Default mode - tests are failing immediately
     */
    STRICT,
    /**
     * Test are failing only at the end of the methods.
     */
    SOFT
  }

  /**
   * Assertion mode - STRICT or SOFT Asserts
   * Default value: STRICT
   *
   * @see AssertionMode
   */
  public static AssertionMode assertionMode = STRICT;

  public enum FileDownloadMode {
    /**
     * Download files via direct http request.
     * Works only for <a href></a> elements.
     * Sends GET request to "href" with all cookies from current browser session.
     */
    HTTPGET,

    /**
     * Download files via selenide embedded proxy server.
     * Works for any elements (e.g. form submission).
     * Doesn't work if you are using custom webdriver without selenide proxy server.
     */
    PROXY
  }

  /**
   * Defines if files are downloaded via direct HTTP or vie selenide emebedded proxy server
   * Can be configured either programmatically or by system property "-Dselenide.fileDownloadMode=PROXY"
   * Default: HTTPGET
   */
  public static FileDownloadMode fileDownload = SELENIDE_CONFIG.fileDownloadMode();

  /**
   * Controls Selenide and WebDriverManager integration.
   * When integration is enabled you don't need to download and setup any browser driver executables.
   * See https://github.com/bonigarcia/webdrivermanager for WebDriverManager configuration details.
   *
   * Default: true
   */
  public static boolean driverManagerEnabled = SELENIDE_CONFIG.driverManagerEnabled();

  /**
   * Enables the ability to run the browser in headless mode.
   * Works only for Chrome(59+) and Firefox(56+).
   *
   * Default: false
   */
  public static boolean headless = SELENIDE_CONFIG.headless();

  /**
   * Sets the path to browser executable.
   * Works only for Chrome, Firefox and Opera.
   */
  public static String browserBinary = SELENIDE_CONFIG.browserBinary();
}
