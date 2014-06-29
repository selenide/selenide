package com.codeborne.selenide.impl;

import org.openqa.selenium.Alert;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

import static com.codeborne.selenide.Configuration.reportsFolder;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.google.common.base.Objects.firstNonNull;
import static java.io.File.separatorChar;
import static org.openqa.selenium.OutputType.FILE;

public class ScreenShotLaboratory {
  protected AtomicLong screenshotCounter = new AtomicLong();
  protected String currentContext = "";
  protected List<String> currentContextScreenshots;
  protected List<String> allScreenshots = new ArrayList<String>();

  protected Set<String> printedErrors = new ConcurrentSkipListSet<String>();

  protected synchronized void printOnce(String action, Throwable error) {
    if (!printedErrors.contains(action)) {
      System.err.println("Failed to " + action + ": ");
      error.printStackTrace();
      printedErrors.add(action);
    }
    else {
      System.err.println("Failed to " + action + ": " + error);
    }
  }

  public String takeScreenShot(String className, String methodName) {
    return takeScreenShot(getScreenshotFileName(className, methodName));
  }

  protected String getScreenshotFileName(String className, String methodName) {
    return className.replace('.', separatorChar) + separatorChar +
        methodName + '.' + timestamp();
  }

  protected long timestamp() {
    return System.currentTimeMillis();
  }

  public String takeScreenShot() {
    return takeScreenShot(generateScreenshotFileName());
  }

  protected String generateScreenshotFileName() {
    return currentContext + timestamp() + "." + screenshotCounter.getAndIncrement();
  }

  /**
   * Takes screenshot of current browser window.
   * Stores 2 files: html of page, and (if possible) image in PNG format.
   * @param fileName name of file (without extension) to store screenshot to.
   * @return the name of last saved file, it's either my_screenshot.png or my_screenshot.html (if failed to create png)
   */
  public String takeScreenShot(String fileName) {
    WebDriver webdriver = getWebDriver();
    if (webdriver == null) {  // TODO it's never null. Use smarter check.
      System.err.println("Cannot take screenshot because browser is not started");
      return null;
    }

    File pageSource = savePageSourceToFile(fileName, webdriver);
    File imageFile = savePageImageToFile(fileName, webdriver);

    String screenshot = firstNonNull(imageFile, pageSource).getAbsolutePath();
    return addToHistory(screenshot);
  }

  protected File savePageImageToFile(String fileName, WebDriver webdriver) {
    File imageFile = null;
    if (webdriver instanceof TakesScreenshot) {
      imageFile = takeScreenshotImage((TakesScreenshot) webdriver, fileName);
    } else if (webdriver instanceof RemoteWebDriver) {
      WebDriver remoteDriver = new Augmenter().augment(webdriver);
      if (remoteDriver instanceof TakesScreenshot) {
        imageFile = takeScreenshotImage((TakesScreenshot) remoteDriver, fileName);
      }
    }
    return imageFile;
  }

  protected File savePageSourceToFile(String fileName, WebDriver webdriver) {
    return savePageSourceToFile(fileName, webdriver, true);
  }

  protected File savePageSourceToFile(String fileName, WebDriver webdriver, boolean retryIfAlert) {
    File pageSource = new File(reportsFolder, fileName + ".html");

    try {
      writeToFile(webdriver.getPageSource(), pageSource);
    } catch (UnhandledAlertException e) {
      if (retryIfAlert) {
        try {
          Alert alert = webdriver.switchTo().alert();
          System.err.println(e + ": " + alert.getText());
          alert.accept();
          savePageSourceToFile(fileName, webdriver, false);
        }
        catch (Exception unableToCloseAlert) {
          System.err.println("Failed to close alert: " + unableToCloseAlert);
        }
      }
      else {
        printOnce("savePageSourceToFile", e);
      }
    }
    catch (UnreachableBrowserException e) {
      writeToFile(e.toString(), pageSource);
      return pageSource;
    }
    catch (Exception e) {
      writeToFile(e.toString(), pageSource);
      printOnce("savePageSourceToFile", e);
    }
    return pageSource;
  }

  protected String addToHistory(String screenshot) {
    if (currentContextScreenshots != null) {
      currentContextScreenshots.add(screenshot);
    }
    allScreenshots.add(screenshot);
    return screenshot;
  }

  protected File takeScreenshotImage(TakesScreenshot driver, String fileName) {
    try {
      File scrFile = driver.getScreenshotAs(FILE);
      File imageFile = new File(reportsFolder, fileName + ".png");
      copyFile(scrFile, imageFile);
      return imageFile;
    } catch (Exception e) {
      printOnce("takeScreenshotImage", e);
      return null;
    }
  }

  protected void copyFile(File sourceFile, File targetFile) throws IOException {
    copyFile(new FileInputStream(sourceFile), targetFile);
  }

  protected void copyFile(InputStream in, File targetFile) throws IOException {
    ensureFolderExists(targetFile);

    try {
      final FileOutputStream out = new FileOutputStream(targetFile);
      try {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
          out.write(buffer, 0, len);
        }
      } finally {
        out.close();
      }
    } finally {
      in.close();
    }
  }

  protected void writeToFile(String content, File targetFile) {
    try {
      copyFile(new ByteArrayInputStream(content.getBytes("UTF-8")), targetFile);
    }
    catch (IOException e) {
      System.err.println("Failed to write file " + targetFile.getAbsolutePath());
      e.printStackTrace();
    }
  }

  protected File ensureFolderExists(File targetFile) {
    File folder = targetFile.getParentFile();
    if (!folder.exists()) {
      System.err.println("Creating folder: " + folder);
      if (!folder.mkdirs()) {
        System.err.println("Failed to create " + folder);
      }
    }
    return targetFile;
  }

  public void startContext(String className, String methodName) {
    String context = className.replace('.', separatorChar) + separatorChar + methodName + separatorChar;
    startContext(context);
  }

  public void startContext(String context) {
    this.currentContext = context;
    currentContextScreenshots = new ArrayList<String>();
  }

  public List<String> finishContext() {
    List<String> result = currentContextScreenshots;
    this.currentContext = "";
    currentContextScreenshots = null;
    return result;
  }

  public List<String> getScreenshots() {
    return allScreenshots;
  }
}
