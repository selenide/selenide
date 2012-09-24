package com.codeborne.selenide;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.apache.commons.io.FileUtils.copyFile;

public class WebDriverRunner {
  public static final String CHROME = "chrome";
  public static final String INTERNET_EXPLORER = "ie";
  public static final String HTMLUNIT = "htmlunit";
  public static final String FIREFOX = "firefox";

  /**
   * If holdBrowserOpen is true, browser window stays open after running tests. It may be useful for debugging.
   * Can be configured either programmatically or by system property "-Dselenide.holdBrowserOpen=true".
   * <p/>
   * Default value: false.
   */
  public static boolean holdBrowserOpen = Boolean.getBoolean("selenide.holdBrowserOpen");

  /**
   * Which browser to use.
   * Can be configured either programmatically or by system property "-Dbrowser=ie".
   * Supported values: "chrome", "firefox", "ie", "htmlunit"
   * <p/>
   * Default value: "firefox"
   */
  public static String browser = System.getProperty("browser", "firefox");

  /**
   * URL of remote web driver (in case of using Selenium Grid).
   * Can be configured either programmatically or by system property "-Dremote=true".
   *
   * Default value: null (Grid is not used).
   */
  public static String remote = System.getProperty("remote");

  /**
   * Value of "chrome.switches" parameter (in case of using Chrome driver).
   * Can be configured either programmatically or by system property "-Dchrome.switches=--start-maximized".
   * Default value: "--start-maximized"
   */
  public static String chromeSwitches = System.getProperty("chrome.switches", "--start-maximized");

  /**
   * Folder to store screenshots to.
   * Can be configured either programmatically or by system property "-Dselenide.reports=true".
   *
   * Default value: "build/reports/tests" (this is default for Gradle projects)
   */
  public static String reportsFolder = System.getProperty("selenide.reports", "build/reports/tests");

  private static WebDriver webdriver;

  static {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        closeWebDriver();
      }
    });
  }

  public static WebDriver getWebDriver() {
    if (webdriver == null) {
      webdriver = createDriver();
    }
    return webdriver;
  }

  public static void closeWebDriver() {
    if (webdriver != null) {
      if (!holdBrowserOpen) {
        webdriver.close();
      }
      webdriver = null;
    }
  }

  public static boolean ie() {
    return webdriver != null && webdriver instanceof InternetExplorerDriver;
  }

  public static void clearBrowserCache() {
    if (webdriver != null) {
      webdriver.manage().deleteAllCookies();
    }
  }

  public static String takeScreenShot(String fileName) {
    if (webdriver == null) {
      return null;
    }

    File targetFile = new File(reportsFolder, fileName + ".html");
    writeToFile(webdriver.getPageSource(), targetFile);

    if (webdriver instanceof TakesScreenshot) {
      try {
        File scrFile = ((TakesScreenshot) webdriver).getScreenshotAs(OutputType.FILE);
        targetFile = new File(reportsFolder, fileName + ".png");
        copyFile(scrFile, targetFile);
      } catch (Exception e) {
        System.err.println(e);
      }
    } else {
      System.err.println("Cannot take screenshot, driver does not support it: " + webdriver);
    }

    return targetFile.getAbsolutePath();
  }

  private static void writeToFile(String content, File targetFile) {
    try {
      FileWriter output = new FileWriter(targetFile);
      try {
        IOUtils.write(content, output);
      } finally {
        output.close();
      }
    } catch (IOException e) {
      System.err.println("Failed to write page source to file " + targetFile + ": " + e);
    }
  }

  private static WebDriver createDriver() {
    if (remote != null) {
      return createRemoteDriver(remote, browser);
    } else if (CHROME.equalsIgnoreCase(browser)) {
      ChromeOptions options = new ChromeOptions();
      options.addArguments("chrome.switches", chromeSwitches);
      return new ChromeDriver(options);
    } else if (INTERNET_EXPLORER.equalsIgnoreCase(browser)) {
      DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
      ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
      return new InternetExplorerDriver(ieCapabilities);
    } else if (HTMLUNIT.equalsIgnoreCase(browser)) {
      DesiredCapabilities desiredCapabilities = DesiredCapabilities.htmlUnit();
      desiredCapabilities.setCapability(HtmlUnitDriver.INVALIDSELECTIONERROR, true);
      desiredCapabilities.setCapability(HtmlUnitDriver.INVALIDXPATHERROR, false);
      desiredCapabilities.setJavascriptEnabled(true);
      return new HtmlUnitDriver(desiredCapabilities);
    } else if (FIREFOX.equalsIgnoreCase(browser)) {
      return new FirefoxDriver();
    } else {
      throw new IllegalArgumentException("Unknown 'browser' parameter: " + browser);
    }
  }

  private static WebDriver createRemoteDriver(String remote, String browser) {
    try {
      DesiredCapabilities capabilities = new DesiredCapabilities();
      capabilities.setBrowserName(browser);
      return new RemoteWebDriver(new URL(remote), capabilities);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid 'remote' parameter: " + remote, e);
    }
  }

  static <T> T fail(String message) {
    if (webdriver == null) {
      Assert.fail(message);
    } else {
      Assert.fail(message +
          ", browser.currentUrl=" + webdriver.getCurrentUrl() +
          ", browser.title=" + webdriver.getTitle()
      );
    }
    return null;
  }
}
