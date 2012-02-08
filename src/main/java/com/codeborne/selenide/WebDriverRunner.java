package com.codeborne.selenide;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FileWriter;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.io.FileUtils.copyFile;

public class WebDriverRunner {
  static String browser = System.getProperty("browser", "firefox");
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
      webdriver = createDriver(browser);
      webdriver.manage().timeouts().implicitlyWait(5, SECONDS);
    }
    return webdriver;
  }

  public static void closeWebDriver() {
    if (webdriver != null) {
      webdriver.close();
      webdriver = null;
    }
  }

  static boolean ie() {
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
      return new ChromeDriver();
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
