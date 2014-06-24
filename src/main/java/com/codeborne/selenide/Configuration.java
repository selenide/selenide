package com.codeborne.selenide;

import java.util.logging.Logger;

import static com.codeborne.selenide.WebDriverRunner.FIREFOX;

public class Configuration {
  private static final Logger LOG = Logger.getLogger(Configuration.class.getName());

  public static String baseUrl = System.getProperty("selenide.baseUrl", "http://localhost:8080");

  public static long timeout = Long.parseLong(System.getProperty("selenide.timeout", "4000"));

  public static long pollingInterval = Long.parseLong(System.getProperty("selenide.pollingInterval", "100"));

  /**
   * If holdBrowserOpen is true, browser window stays open after running tests. It may be useful for debugging.
   * Can be configured either programmatically or by system property "-Dselenide.holdBrowserOpen=true".
   * <p/>
   * Default value: false.
   */
  public static boolean holdBrowserOpen = Boolean.getBoolean("selenide.holdBrowserOpen");

  /**
   * Which browser to use.
   * Can be configured either programmatically or by system property "-Dselenide.browser=ie" or "-Dbrowser=ie".
   * Supported values: "chrome", "firefox", "ie", "htmlunit", "phantomjs", "opera"
   * <p/>
   * Default value: "firefox"
   */
  public static String browser = System.getProperty("selenide.browser", System.getProperty("browser", FIREFOX));

  /**
   * URL of remote web driver (in case of using Selenium Grid).
   * Can be configured either programmatically or by system property "-Dremote=http://localhost:5678/hub".
   *
   * Default value: null (Grid is not used).
   */
  public static String remote = System.getProperty("remote");

  /**
   * The browser window is maximized when started.
   * Can be configured either programmatically or by system property "-Dselenide.start-maximized=true".
   *
   * Default value: true
   */
  public static boolean startMaximized = Boolean.parseBoolean(System.getProperty("selenide.start-maximized", "true"));

  /**
   * Does Selenide need to take screenshots on failing tests.
   *
   * Default value: true
   */
  public static boolean screenshots = Boolean.parseBoolean(System.getProperty("selenide.screenshots", "true"));

  /**
   * Folder to store screenshots to.
   * Can be configured either programmatically or by system property "-Dselenide.reports=true".
   *
   * Default value: "build/reports/tests" (this is default for Gradle projects)
   */
  public static String reportsFolder = System.getProperty("selenide.reports", "build/reports/tests");

  /**
   * Optional: URL of CI server where reports are published to.
   * In case of Jenkins, it is "BUILD_URL/artifact" by default.
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
    return s == null || s.trim().length() == 0;
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
   * Default value: false (true for headless browsers like HtmlUnit and PhantomJS because they do not support alert/confirm anyway)
   */
  public static boolean dismissModalDialogs = Boolean.parseBoolean(System.getProperty("selenide.dismissModalDialogs", "false"));
}
