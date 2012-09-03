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
import java.net.URL;

import static org.apache.commons.io.FileUtils.copyFile;

public class WebDriverRunner {
  /**
   * If holdBrowserOpen is true, browser window stays open after running tests. It may be useful for debugging.
   * Can be configured either programmatically or by system property "-Dselenide.holdBrowserOpen=true".
   *
   * Default value: false.
   */
  public static boolean holdBrowserOpen = Boolean.getBoolean("selenide.holdBrowserOpen");

  static String browser = System.getProperty("browser", "firefox");
  static String remote = System.getProperty("remote", null);
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
      if (remote == null) {
        webdriver = createDriver(browser);
      } else {
        try {
          webdriver = createRemoteDriver(remote, browser);
        } catch (Exception ex){
          throw new DriverInitializationException("Could not initialize remote driver", ex);
        }
      }
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
    } else if (webdriver instanceof TakesScreenshot) {
      try {
        File scrFile = ((TakesScreenshot) webdriver).getScreenshotAs(OutputType.FILE);
        String pageSource = webdriver.getPageSource();

        String screenshotFileName = "build/reports/tests/" + fileName + ".png";
        String htmlFileName = "build/reports/tests/" + fileName + ".html";
        copyFile(scrFile, new File(screenshotFileName));
        IOUtils.write(pageSource, new FileWriter(htmlFileName));
        return screenshotFileName;
      } catch (Exception e) {
        System.err.println(e);
      }
    } else {
      System.err.println("Cannot take screenshot, driver does not support it: " + webdriver);
    }

    return null;
  }

  private static WebDriver createDriver(String browser) {
    if ("chrome".equalsIgnoreCase(browser)) {
      ChromeOptions options = new ChromeOptions();
      options.addArguments("chrome.switches", "--start-maximized");
      return new ChromeDriver(options);
    }
    else if ("ie".equalsIgnoreCase(browser)) {
      DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
      ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
      return new InternetExplorerDriver(ieCapabilities);
    }
    else if ("htmlunit".equalsIgnoreCase(browser)) {
      DesiredCapabilities desiredCapabilities = DesiredCapabilities.htmlUnit();
      desiredCapabilities.setCapability(HtmlUnitDriver.INVALIDSELECTIONERROR, true);
      desiredCapabilities.setCapability(HtmlUnitDriver.INVALIDXPATHERROR, false);
      desiredCapabilities.setJavascriptEnabled(true);
      return new HtmlUnitDriver(desiredCapabilities);
    }
    else {
      return new FirefoxDriver();
    }
  }

  private static WebDriver createRemoteDriver(String remote, String browser) throws Exception {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setBrowserName(browser);
    return new RemoteWebDriver(new URL(remote), capabilities);
  }

  static <T> T fail(String message) {
    if (webdriver == null) {
      Assert.fail(message);
    }
    else {
      Assert.fail(message +
          ", browser.currentUrl=" + webdriver.getCurrentUrl() +
          ", browser.title=" + webdriver.getTitle()
      );
    }
    return null;
  }
}
