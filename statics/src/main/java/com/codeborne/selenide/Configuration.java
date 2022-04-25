package com.codeborne.selenide;

import org.openqa.selenium.MutableCapabilities;

/**
 * Configuration settings for Selenide default browser
 * <br>
 * This class is designed so that every setting can be set either via system property or programmatically.
 * <br>
 * Please note that all fields are static, meaning that
 * every change will immediately reflect in all threads (if you run tests in parallel).
 *
 * <p>
 *   These system properties can be additonally used having effect on every new created browser in test.
 *   For example as -D&lt;property&gt;=&lt;value&gt; in command-line
 * </p>
 * <p>
 *  <b>chromeoptions.args</b> - Sets the arguments for chrome options, parameters are comma separated
 *  If comma is a part of the value, use double quotes around the argument
 *  Non-official list of parameters can be found at https://peter.sh/experiments/chromium-command-line-switches/
 *
 *  Example: --no-sandbox,--disable-3d-apis,"--user-agent=Firefox 45, Mozilla"
 * </p>
 * <p>
 *  <b>chromeoptions.prefs</b> - Sets the preferences for chrome options, which are comma separated
 *   keyX=valueX preferences. If comma is a part of the value, use double quotes around the preference
 *   List of preferences can be found at
 *   https://chromium.googlesource.com/chromium/src/+/master/chrome/common/pref_names.cc
 *
 *   Example: homepage=http://google.com,"intl.allowed_languages=en,ru,es"
 * </p>
 */
public class Configuration {
  private static final SelenideConfig defaults = new SelenideConfig();

  /**
   * Base url for open() function calls
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.baseUrl=http://myhost".
   * <br>
   * Default value: http://localhost:8080
   */
  public static String baseUrl = defaults.baseUrl();

  /**
   * Timeout in milliseconds to fail the test, if conditions still not met
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.timeout=10000"
   * <br>
   * Default value: 4000 (milliseconds)
   */
  public static long timeout = defaults.timeout();

  /**
   * Interval in milliseconds, when checking if a single element or collection elements are appeared
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.pollingInterval=50"
   * <br>
   * Default value: 200 (milliseconds)
   */
  public static long pollingInterval = defaults.pollingInterval();

  /**
   * If holdBrowserOpen is true, browser window stays open after running tests. It may be useful for debugging.
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.holdBrowserOpen=true".
   * <br>
   * Default value: false.
   */
  public static boolean holdBrowserOpen = defaults.holdBrowserOpen();

  /**
   * Should Selenide re-spawn browser if it's disappeared (hangs, broken, unexpectedly closed).
   * <br>
   * Can be configured either programmatically, via selenide.properties file
   * or by system property "-Dselenide.reopenBrowserOnFail=false".
   * <br>
   * Set this property to false if you want to disable automatic re-spawning the browser.
   * <br>
   * Default value: true
   */
  public static boolean reopenBrowserOnFail = defaults.reopenBrowserOnFail();

  /**
   * Which browser to use.
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.browser=ie".
   * Supported values: "chrome", "firefox", "ie", "opera", "edge"
   * <br>
   * Default value: "chrome"
   */
  public static String browser = defaults.browser();

  /**
   * Which browser version to use (for Internet Explorer).
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.browserVersion=8".
   * <br>
   * Default value: none
   */
  public static String browserVersion = defaults.browserVersion();

  /**
   * URL of remote web driver (in case of using Selenium Grid).
   * Can be configured either programmatically, via selenide.properties file
   * or by system property "-Dselenide.remote=http://localhost:5678/wd/hub".
   * <br>
   * Default value: null (Grid is not used).
   */
  public static String remote = defaults.remote();

  /**
   * The browser window size.
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.browserSize=1024x768".
   * <br>
   * Default value: 1366x768
   */
  public static String browserSize = defaults.browserSize();

  /**
   * The browser window position on screen.
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.browserPosition=10x10".
   * <br>
   * Default value: none
   */
  public static String browserPosition = defaults.browserPosition();

  /**
   * Browser capabilities.
   * Warning: this capabilities will override capabilities were set by system properties.
   * <br>
   * Default value: new MutableCapabilities()
   */
  public static MutableCapabilities browserCapabilities = defaults.browserCapabilities();

  /**
   * Should webdriver wait until page is completely loaded.
   * Possible values: "none", "normal" and "eager".
   * <br>
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.pageLoadStrategy=eager".
   * Default value: "normal".
   * <br>
   * - `normal`: return after the load event fires on the new page (it's default in Selenium webdriver);
   * - `eager`: return after DOMContentLoaded fires;
   * - `none`: return immediately
   * <br>
   * In some cases `eager` can bring performance boosts for the slow tests.
   * Though, we left default value `normal` because we are afraid to break users' existing tests.
   * <br>
   * See https://w3c.github.io/webdriver/webdriver-spec.html#dfn-page-loading-strategy
   *
   * @since 3.5
   */
  public static String pageLoadStrategy = defaults.pageLoadStrategy();

  /**
   * Timeout for loading a web page (in milliseconds).
   * Default timeout in Selenium WebDriver is 300 seconds (which is incredibly long).
   * Selenide default is 30 seconds.
   *
   * @since 5.15.0
   */
  public static long pageLoadTimeout = defaults.pageLoadTimeout();

  /**
   * ATTENTION! Automatic WebDriver waiting after click isn't working in case of using this feature.
   * Use clicking via JavaScript instead common element clicking.
   * This solution may be helpful for testing in Internet Explorer.
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.clickViaJs=true".
   * <br>
   * Default value: false
   */
  public static boolean clickViaJs = defaults.clickViaJs();

  /**
   * Defines if Selenide takes screenshots on failing tests.
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.screenshots=false".
   * <br>
   * Default value: true
   */
  public static boolean screenshots = defaults.screenshots();

  /**
   * Defines if Selenide saves page source on failing tests.
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.savePageSource=false".
   * <br>
   * Default value: true
   */
  public static boolean savePageSource = defaults.savePageSource();

  /**
   * Folder to store screenshots to.
   * Can be configured either programmatically, via selenide.properties file
   * or by system property "-Dselenide.reportsFolder=test-result/reports".
   * <br>
   * Default value: "build/reports/tests" (this is default for Gradle projects)
   */
  public static String reportsFolder = defaults.reportsFolder();

    /**
   * Folder to store downloaded files to.
   * Can be configured either programmatically, via selenide.properties file
     * or by system property "-Dselenide.downloadsFolder=test-result/downloads".
   * <br>
   * Default value: "build/downloads" (this is default for Gradle projects)
   */
  public static String downloadsFolder = defaults.downloadsFolder();

  /**
   * Optional: URL of CI server where reports are published to.
   * In case of Jenkins, it is "BUILD_URL/artifact" by default.
   * <br>
   * Can be configured either programmatically, via selenide.properties file
   * or by system property "-Dselenide.reportsUrl=http://jenkins-host/reports".
   * <br>
   * If it's given, names of screenshots are printed as
   * "http://ci.mycompany.com/job/my-job/446/artifact/build/reports/tests/my_test.png" - it's useful to analyze test
   * failures in CI server.
   */
  public static String reportsUrl = defaults.reportsUrl();

  /**
   * If set to true, sets value by javascript instead of using Selenium built-in "sendKey" function
   * (that is quite slow because it sends every character separately).
   * <br>
   * Tested on Codeborne projects - works well, speed up ~30%.
   * Some people reported 150% speedup (because sending characters one-by-one was especially
   * slow via network to Selenium Grid on cloud).
   * <br>
   * https://github.com/selenide/selenide/issues/135
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.fastSetValue=true".
   * <br>
   * Default value: false
   */
  public static boolean fastSetValue = defaults.fastSetValue();

  /**
   * Define behaviour of {@code $.shouldHave(text)}: full text or partial text.
   *
   * @since 6.5.0
   */
  public static TextCheck textCheck = defaults.textCheck();

  /**
   * <p>Choose how Selenide should retrieve web elements: using default CSS or Sizzle (CSS3).</p>
   * <br>
   * <p>
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.selectorMode=Sizzle".
   * </p>
   * <br>
   *   Possible values: "CSS" or "Sizzle"
   * <br>
   *   Default value: CSS
   *
   * @see SelectorMode
   */
  public static SelectorMode selectorMode = defaults.selectorMode();

  /**
   * <p>Assertion mode</p>
   *
   * <p>Can be configured either programmatically, via selenide.properties file
   * or by system property "-Dselenide.assertionMode=SOFT".</p>
   *
   * <br>
   *   Possible values: "STRICT" or "SOFT"
   * <br>
   *   Default value: STRICT
   *
   * @see AssertionMode
   */
  public static AssertionMode assertionMode = defaults.assertionMode();

  /**
   * Defines if files are downloaded via direct HTTP or vie selenide embedded proxy server
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.fileDownload=PROXY"
   * <br>
   * Default: HTTPGET
   */
  public static FileDownloadMode fileDownload = defaults.fileDownload();

  /**
   * If Selenide should run browser through its own proxy server.
   * It allows some additional features which are not possible with plain Selenium.
   * But it's not enabled by default because sometimes it would not work (more exactly, if tests and browser and
   * executed on different machines, and "test machine" is not accessible from "browser machine"). If it's not your
   * case, I recommend to enable proxy.
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.proxyEnabled=true"
   * <br>
   * Default: false
   */
  public static boolean proxyEnabled = defaults.proxyEnabled();

  /**
   * Host of Selenide proxy server.
   * Used only if proxyEnabled == true.
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.proxyHost=127.0.0.1"
   * <br>
   * Default: empty (meaning that Selenide will detect current machine's ip/hostname automatically)
   *
   * @see com.browserup.bup.client.ClientUtil#getConnectableAddress()
   */
  public static String proxyHost = defaults.proxyHost();

  /**
   * Port of Selenide proxy server.
   * Used only if proxyEnabled == true.
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.proxyPort=8888"
   * <br>
   * Default: 0 (meaning that Selenide will choose a random free port on current machine)
   */
  public static int proxyPort = defaults.proxyPort();

  /**
   * Controls Selenide and WebDriverManager integration.
   * When integration is enabled you don't need to download and setup any browser driver executables.
   * See https://github.com/bonigarcia/webdrivermanager for WebDriverManager configuration details.
   * Can be configured either programmatically, via selenide.properties file
   * or by system property "-Dselenide.driverManagerEnabled=false"
   * <br>
   *
   * Default: true
   */
  public static boolean driverManagerEnabled = defaults.driverManagerEnabled();

  /**
   * <p>
   *  Whether webdriver logs should be enabled.
   * </p>
   *
   * <p>
   *   These logs may be useful for debugging some webdriver issues.
   *   But in most cases they are not needed (and can take quite a lot of disk space),
   *   that's why don't enable them by default.
   * </p>
   *
   * Default: false
   * @since 5.18.0
   */
  public static boolean webdriverLogsEnabled = defaults.webdriverLogsEnabled();

  /**
   * Enables the ability to run the browser in headless mode.
   * Works only for Chrome(59+) and Firefox(56+).
   * Can be configured either programmatically, via selenide.properties file or by system property "-Dselenide.headless=true"
   * <br>
   * Default: false
   */
  public static boolean headless = defaults.headless();

  /**
   * Sets the path to browser executable.
   * Works only for Chrome, Firefox and Opera.
   * Can be configured either programmatically, via selenide.properties file
   * or by system property "-Dselenide.browserBinary=/path/to/binary"
   */
  public static String browserBinary = defaults.browserBinary();

}
