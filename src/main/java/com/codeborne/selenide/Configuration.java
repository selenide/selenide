package com.codeborne.selenide;

import java.util.logging.Logger;
import org.openqa.selenium.remote.DesiredCapabilities;

import static com.codeborne.selenide.Configuration.AssertionMode.STRICT;
import static com.codeborne.selenide.Configuration.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.Configuration.SelectorMode.CSS;
import static com.codeborne.selenide.Browsers.FIREFOX;

public class Configuration {
  private static final Logger LOG = Logger.getLogger(Configuration.class.getName());

  /**
   * Base url for open() function calls
   * Can be configured either programmatically or by system property "-Dselenide.baseUrl=http://myhost".
   * Default value: http://localhost:8080
   */
  public static String baseUrl = System.getProperty("selenide.baseUrl", "http://localhost:8080");

  /**
   * Timeout in milliseconds for a collection to get completely loaded
   * Conditions will be checked at this point at latest, even if they are still loading
   * Can be configured either programmatically or by system property "-Dselenide.collectionsTimeout=10000"
   * Default value: 6000 (milliseconds)
   */
  public static long collectionsTimeout = Long.parseLong(System.getProperty("selenide.collectionsTimeout", "6000"));

  /**
   * Timeout in milliseconds to fail the test, if conditions still not met
   * Can be configured either programmatically or by system property "-Dselenide.timeout=10000"
   * Default value: 4000 (milliseconds)
   */
  public static long timeout = Long.parseLong(System.getProperty("selenide.timeout", "4000"));

  /**
   * Interval in milliseconds, when checking if a single element is appeared
   * Can be configured either programmatically or by system property "-Dselenide.pollingInterval=50"
   * Default value: 100 (milliseconds)
   */
  public static long pollingInterval = Long.parseLong(System.getProperty("selenide.pollingInterval", "100"));

  /**
   * Interval in milliseconds, when checking if a new collection elements appeared
   * Can be configured either programmatically or by system property "-Dselenide.collectionsPollingInterval=150"
   * Default value: 200 (milliseconds)
   */
  public static long collectionsPollingInterval = Long.parseLong(
          System.getProperty("selenide.collectionsPollingInterval", "200"));

  /**
   * If holdBrowserOpen is true, browser window stays open after running tests. It may be useful for debugging.
   * Can be configured either programmatically or by system property "-Dselenide.holdBrowserOpen=true".
   * <br>
   * Default value: false.
   */
  public static boolean holdBrowserOpen = Boolean.getBoolean("selenide.holdBrowserOpen");

  /**
   * Should Selenide re-spawn browser if it's disappeared (hangs, broken, unexpectedly closed).
   * <br>
   * Can be configured either programmatically or by system property "-Dselenide.reopenBrowserOnFail=false".
   * <br>
   * Default value: true
   * Set this property to false if you want to disable automatic re-spawning the browser.
   */
  public static boolean reopenBrowserOnFail = Boolean.parseBoolean(
      System.getProperty("selenide.reopenBrowserOnFail", "true"));

  /**
   * Timeout (in milliseconds) for closing/killing browser.
   * <br>
   * Sometimes we have problems with calling driver.close() or driver.quit() method, and test always is suspended too long.
   * <br>
   * Can be configured either programmatically or by system property "-Dselenide.closeBrowserTimeout=10000"
   * Default value: 5000 (milliseconds)
   */
  public static long closeBrowserTimeoutMs = Long.parseLong(System.getProperty("selenide.closeBrowserTimeout", "5000"));

  /**
   * Which browser to use.
   * Can be configured either programmatically or by system property "-Dselenide.browser=ie" or "-Dbrowser=ie".
   * Supported values: "chrome", "firefox", "legacy_firefox", "ie", "htmlunit", "phantomjs", "opera", "safari", "edge", "jbrowser"
   * <br>
   * Default value: "firefox"
   */
  public static String browser = System.getProperty("selenide.browser", System.getProperty("browser", FIREFOX));

  /**
   * Which browser version to use (for Internet Explorer).
   * Can be configured either programmatically or by system property "-Dselenide.browserVersion=8" or "-Dbrowser.version=8".
   * <br>
   * Default value: none
   */
  public static String browserVersion = System.getProperty("selenide.browserVersion",
          System.getProperty("selenide.browser.version", System.getProperty("browser.version")));

  /**
   * URL of remote web driver (in case of using Selenium Grid).
   * Can be configured either programmatically or by system property "-Dremote=http://localhost:5678/wd/hub".
   *
   * Default value: null (Grid is not used).
   */
  public static String remote = System.getProperty("remote");

  /**
   * The browser window size.
   * Can be configured either programmatically or by system property "-Dselenide.browserSize=1024x768".
   *
   * Default value: none (browser size will not be set explicitly)
   */
  public static String browserSize = System.getProperty("selenide.browserSize",
          System.getProperty("selenide.browser-size"));

  /**
   * The browser window position on screen.
   * Can be configured either programmatically or by system property "-Dselenide.browserPosition=10x10".
   *
   * Default value: none (browser window position will not be set explicitly)
   */
  public static String browserPosition = System.getProperty("selenide.browserPosition");

  /**
   * The browser window is maximized when started.
   * Can be configured either programmatically or by system property "-Dselenide.startMaximized=true".
   * <p>
   * Default value: true
   */
  public static boolean startMaximized = Boolean.parseBoolean(System.getProperty("selenide.startMaximized",
          System.getProperty("selenide.start-maximized", "true")));

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
  public static String chromeSwitches = System.getProperty("selenide.chrome.switches", System.getProperty("chrome.switches"));

  /**
   * Browser capabilities.
   * Warning: this capabilities will override capabilities were set by system properties.
   * <br>
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
  public static String pageLoadStrategy = System.getProperty("selenide.pageLoadStrategy",
          System.getProperty("selenide.page-load-strategy", "normal"));

  /**
   * ATTENTION! Automatic WebDriver waiting after click isn't working in case of using this feature.
   * Use clicking via JavaScript instead common element clicking.
   * This solution may be helpful for testing in Internet Explorer.
   * Can be configured either programmatically or by system property "-Dselenide.clickViaJs=true".
   * Default value: false
   */
  public static boolean clickViaJs = Boolean.parseBoolean(System.getProperty("selenide.clickViaJs",
          System.getProperty("selenide.click-via-js", "false")));

  /**
   * Defines if Selenide tries to capture JS errors
   * Can be configured either programmatically or by system property "-Dselenide.captureJavascriptErrors=false".
   *
   * Default value: true
   */
  public static boolean captureJavascriptErrors = Boolean.parseBoolean(System.getProperty("selenide.captureJavascriptErrors", "true"));

  /**
   * Defines if Selenide takes screenshots on failing tests.
   * Can be configured either programmatically or by system property "-Dselenide.screenshots=false".
   *
   * Default value: true
   */
  public static boolean screenshots = Boolean.parseBoolean(System.getProperty("selenide.screenshots", "true"));

  /**
   * Defines if Selenide saves page source on failing tests.
   * Can be configured either programmatically or by system property "-Dselenide.savePageSource=false".
   * Default value: true
   */
  public static boolean savePageSource = Boolean.parseBoolean(System.getProperty("selenide.savePageSource", "true"));

  /**
   * Folder to store screenshots to.
   * Can be configured either programmatically or by system property "-Dselenide.reportsFolder=test-result/reports".
   *
   * Default value: "build/reports/tests" (this is default for Gradle projects)
   */
  public static String reportsFolder = System.getProperty("selenide.reportsFolder",
          System.getProperty("selenide.reports", "build/reports/tests"));

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
  public static boolean fastSetValue = Boolean.parseBoolean(System.getProperty("selenide.fastSetValue", "false"));

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
  public static boolean versatileSetValue = Boolean.parseBoolean(System.getProperty("selenide.versatileSetValue", "false"));

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
  public static boolean setValueChangeEvent = Boolean.parseBoolean(System.getProperty("selenide.setValueChangeEvent", "true"));

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
   * Can be configured either programmatically or by system property "-Dselenide.fileDownload=PROXY"
   * Default: HTTPGET
   */
  public static FileDownloadMode fileDownload = FileDownloadMode.valueOf(
          System.getProperty("selenide.fileDownload", HTTPGET.name()));

  /**
   * If Selenide should run browser through its own proxy server.
   * It allows some additional features which are not possible with plain Selenium.
   * But it's not enabled by default because sometimes it would not work (more exactly, if tests and browser and
   * executed on different machines, and "test machine" is not accessible from "browser machine"). If it's not your
   * case, I recommend to enable proxy.
   *
   * Default: false
   */
  public static boolean proxyEnabled = Boolean.parseBoolean(System.getProperty("selenide.proxyEnabled", "false"));

  /**
   * Host of Selenide proxy server.
   * Used only if proxyEnabled == true.
   *
   * Default: empty (meaning that Selenide will detect current machine's ip/hostname automatically)
   *
   * @see net.lightbody.bmp.client.ClientUtil#getConnectableAddress()
   */
  public static String proxyHost = System.getProperty("selenide.proxyHost", "");

  /**
   * Port of Selenide proxy server.
   * Used only if proxyEnabled == true.
   *
   * Default: 0 (meaning that Selenide will choose a random free port on current machine)
   */
  public static int proxyPort = Integer.parseInt(System.getProperty("selenide.proxyPort", "0"));

  /**
   * Controls Selenide and WebDriverManager integration.
   * When integration is enabled you don't need to download and setup any browser driver executables.
   * See https://github.com/bonigarcia/webdrivermanager for WebDriverManager configuration details.
   *
   * Default: true
   */
  public static boolean driverManagerEnabled = Boolean.parseBoolean(System.getProperty("selenide.driverManagerEnabled", "true"));

  /**
   * Enables the ability to run the browser in headless mode.
   * Works only for Chrome(59+) and Firefox(56+).
   *
   * Default: false
   */
  public static boolean headless = Boolean.parseBoolean(System.getProperty("selenide.headless", "false"));

  /**
   * Sets the path to browser executable.
   * Works only for Chrome, Firefox and Opera.
   */
  public static String browserBinary = System.getProperty("selenide.browserBinary", "");
}
