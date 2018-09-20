package com.codeborne.selenide;

import org.openqa.selenium.remote.DesiredCapabilities;

public class Configuration {
  private static SelenideConfig defaults = new SelenideConfig();

  /**
   * Base url for open() function calls
   * Can be configured either programmatically or by system property "-Dselenide.baseUrl=http://myhost".
   * Default value: http://localhost:8080
   */
  public static String baseUrl = defaults.baseUrl();

  /**
   * Timeout in milliseconds for a collection to get completely loaded
   * Conditions will be checked at this point at latest, even if they are still loading
   * Can be configured either programmatically or by system property "-Dselenide.collectionsTimeout=10000"
   * Default value: 6000 (milliseconds)
   */
  public static long collectionsTimeout = defaults.collectionsTimeout();

  /**
   * Timeout in milliseconds to fail the test, if conditions still not met
   * Can be configured either programmatically or by system property "-Dselenide.timeout=10000"
   * Default value: 4000 (milliseconds)
   */
  public static long timeout = defaults.timeout();

  /**
   * Interval in milliseconds, when checking if a single element is appeared
   * Can be configured either programmatically or by system property "-Dselenide.pollingInterval=50"
   * Default value: 100 (milliseconds)
   */
  public static long pollingInterval = defaults.pollingInterval();

  /**
   * Interval in milliseconds, when checking if a new collection elements appeared
   * Can be configured either programmatically or by system property "-Dselenide.collectionsPollingInterval=150"
   * Default value: 200 (milliseconds)
   */
  public static long collectionsPollingInterval = defaults.collectionsPollingInterval();

  /**
   * If holdBrowserOpen is true, browser window stays open after running tests. It may be useful for debugging.
   * Can be configured either programmatically or by system property "-Dselenide.holdBrowserOpen=true".
   * <br>
   * Default value: false.
   */
  public static boolean holdBrowserOpen = defaults.holdBrowserOpen();

  /**
   * Should Selenide re-spawn browser if it's disappeared (hangs, broken, unexpectedly closed).
   * <br>
   * Can be configured either programmatically or by system property "-Dselenide.reopenBrowserOnFail=false".
   * <br>
   * Default value: true
   * Set this property to false if you want to disable automatic re-spawning the browser.
   */
  public static boolean reopenBrowserOnFail = defaults.reopenBrowserOnFail();

  /**
   * Timeout (in milliseconds) for closing/killing browser.
   * <br>
   * Sometimes we have problems with calling driver.close() or driver.quit() method, and test always is suspended too long.
   * <br>
   * Can be configured either programmatically or by system property "-Dselenide.closeBrowserTimeout=10000"
   * Default value: 5000 (milliseconds)
   */
  public static long closeBrowserTimeoutMs = defaults.closeBrowserTimeoutMs();

  /**
   * Which browser to use.
   * Can be configured either programmatically or by system property "-Dselenide.browser=ie" or "-Dbrowser=ie".
   * Supported values: "chrome", "firefox", "legacy_firefox", "ie", "htmlunit", "phantomjs", "opera", "safari", "edge", "jbrowser"
   * <br>
   * Default value: "firefox"
   */
  public static String browser = defaults.browser();

  /**
   * Which browser version to use (for Internet Explorer).
   * Can be configured either programmatically or by system property "-Dselenide.browserVersion=8" or "-Dbrowser.version=8".
   * <br>
   * Default value: none
   */
  public static String browserVersion = defaults.browserVersion();

  /**
   * URL of remote web driver (in case of using Selenium Grid).
   * Can be configured either programmatically or by system property "-Dselenide.remote=http://localhost:5678/wd/hub".
   *
   * Default value: null (Grid is not used).
   */
  public static String remote = defaults.remote();

  /**
   * The browser window size.
   * Can be configured either programmatically or by system property "-Dselenide.browserSize=1024x768".
   *
   * Default value: none (browser size will not be set explicitly)
   */
  public static String browserSize = defaults.browserSize();

  /**
   * The browser window position on screen.
   * Can be configured either programmatically or by system property "-Dselenide.browserPosition=10x10".
   *
   * Default value: none (browser window position will not be set explicitly)
   */
  public static String browserPosition = defaults.browserPosition();

  /**
   * The browser window is maximized when started.
   * Can be configured either programmatically or by system property "-Dselenide.startMaximized=true".
   * <p>
   * Default value: true
   */
  public static boolean startMaximized = defaults.startMaximized();

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
  public static String chromeSwitches = defaults.chromeSwitches();

  /**
   * Browser capabilities.
   * Warning: this capabilities will override capabilities were set by system properties.
   * <br>
   * Default value: null
   */
  public static DesiredCapabilities browserCapabilities = defaults.browserCapabilities();

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
  public static String pageLoadStrategy = defaults.pageLoadStrategy();

  /**
   * ATTENTION! Automatic WebDriver waiting after click isn't working in case of using this feature.
   * Use clicking via JavaScript instead common element clicking.
   * This solution may be helpful for testing in Internet Explorer.
   * Can be configured either programmatically or by system property "-Dselenide.clickViaJs=true".
   * Default value: false
   */
  public static boolean clickViaJs = defaults.clickViaJs();

  /**
   * Defines if Selenide tries to capture JS errors
   * Can be configured either programmatically or by system property "-Dselenide.captureJavascriptErrors=false".
   *
   * Default value: true
   */
  public static boolean captureJavascriptErrors = defaults.captureJavascriptErrors();

  /**
   * Defines if Selenide takes screenshots on failing tests.
   * Can be configured either programmatically or by system property "-Dselenide.screenshots=false".
   *
   * Default value: true
   */
  public static boolean screenshots = defaults.screenshots();

  /**
   * Defines if Selenide saves page source on failing tests.
   * Can be configured either programmatically or by system property "-Dselenide.savePageSource=false".
   * Default value: true
   */
  public static boolean savePageSource = defaults.savePageSource();

  /**
   * Folder to store screenshots to.
   * Can be configured either programmatically or by system property "-Dselenide.reportsFolder=test-result/reports".
   *
   * Default value: "build/reports/tests" (this is default for Gradle projects)
   */
  public static String reportsFolder = defaults.reportsFolder();

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
  public static String reportsUrl = defaults.reportsUrl();

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
  public static boolean fastSetValue = defaults.fastSetValue();

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
  public static boolean versatileSetValue = defaults.versatileSetValue();

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
  public static boolean setValueChangeEvent = defaults.setValueChangeEvent();

  /**
   * Choose how Selenide should retrieve web elements: using default CSS or Sizzle (CSS3)
   */
  public static SelectorMode selectorMode = defaults.selectorMode();


  /**
   * Assertion mode - STRICT or SOFT Asserts
   * Default value: STRICT
   *
   * @see AssertionMode
   */
  public static AssertionMode assertionMode = defaults.assertionMode();

  /**
   * Defines if files are downloaded via direct HTTP or vie selenide emebedded proxy server
   * Can be configured either programmatically or by system property "-Dselenide.fileDownload=PROXY"
   * Default: HTTPGET
   */
  public static FileDownloadMode fileDownload = defaults.fileDownload();

  /**
   * If Selenide should run browser through its own proxy server.
   * It allows some additional features which are not possible with plain Selenium.
   * But it's not enabled by default because sometimes it would not work (more exactly, if tests and browser and
   * executed on different machines, and "test machine" is not accessible from "browser machine"). If it's not your
   * case, I recommend to enable proxy.
   *
   * Default: false
   */
  public static boolean proxyEnabled = defaults.proxyEnabled();

  /**
   * Host of Selenide proxy server.
   * Used only if proxyEnabled == true.
   *
   * Default: empty (meaning that Selenide will detect current machine's ip/hostname automatically)
   *
   * @see net.lightbody.bmp.client.ClientUtil#getConnectableAddress()
   */
  public static String proxyHost = defaults.proxyHost();

  /**
   * Port of Selenide proxy server.
   * Used only if proxyEnabled == true.
   *
   * Default: 0 (meaning that Selenide will choose a random free port on current machine)
   */
  public static int proxyPort = defaults.proxyPort();

  /**
   * Controls Selenide and WebDriverManager integration.
   * When integration is enabled you don't need to download and setup any browser driver executables.
   * See https://github.com/bonigarcia/webdrivermanager for WebDriverManager configuration details.
   *
   * Default: true
   */
  public static boolean driverManagerEnabled = defaults.driverManagerEnabled();

  /**
   * Enables the ability to run the browser in headless mode.
   * Works only for Chrome(59+) and Firefox(56+).
   *
   * Default: false
   */
  public static boolean headless = defaults.headless();

  /**
   * Sets the path to browser executable.
   * Works only for Chrome, Firefox and Opera.
   */
  public static String browserBinary = defaults.browserBinary();
}
