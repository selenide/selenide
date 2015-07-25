package com.codeborne.selenide.impl;

import com.codeborne.selenide.WebDriverRunner;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.reportsFolder;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.google.common.base.MoreObjects.firstNonNull;
import static java.io.File.separatorChar;
import static java.util.logging.Level.SEVERE;
import static org.openqa.selenium.OutputType.FILE;

public class ScreenShotLaboratory {
  private static final Logger log = Logger.getLogger(ScreenShotLaboratory.class.getName());

  protected AtomicLong screenshotCounter = new AtomicLong();
  protected String currentContext = "";
  protected List<String> currentContextScreenshots;
  protected List<String> allScreenshots = new ArrayList<String>();

  protected Set<String> printedErrors = new ConcurrentSkipListSet<String>();

  protected synchronized void printOnce(String action, Throwable error) {
    if (!printedErrors.contains(action)) {
      log.log(SEVERE, error.getMessage(), error);
      printedErrors.add(action);
    }
    else {
      log.severe("Failed to " + action + ": " + error);
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
    if (!WebDriverRunner.hasWebDriverStarted()) {
      log.warning("Cannot take screenshot because browser is not started");
      return null;
    }

    WebDriver webdriver = getWebDriver();
    File pageSource = savePageSourceToFile(fileName, webdriver);
    File imageFile = savePageImageToFile(fileName, webdriver);

    String screenshot = firstNonNull(imageFile, pageSource).getAbsolutePath();
    return addToHistory(screenshot);
  }
  
  public File takeScreenshot(WebElement element) {
    if (!WebDriverRunner.hasWebDriverStarted()) {
      log.warning("Cannot take screenshot because browser is not started");
      return null;
    }

    WebDriver webdriver = getWebDriver();
    if (!(webdriver instanceof TakesScreenshot)) {
      log.warning("Cannot take screenshot because browser does not support screenshots");
      return null;
    }
    
    File screen = ((TakesScreenshot) webdriver).getScreenshotAs(OutputType.FILE);

    Point p = element.getLocation();
    Dimension elementSize = element.getSize();

    try {
      BufferedImage img = ImageIO.read(screen);
      BufferedImage dest = img.getSubimage(p.getX(), p.getY(), elementSize.getWidth(), elementSize.getHeight());
      ImageIO.write(dest, "png", screen);
      File screenshotOfElement = new File(generateScreenshotFileName());
      FileUtils.copyFile(screen, screenshotOfElement);
      return screenshotOfElement;
    }
    catch (IOException e) {
      printOnce("takeScreenshotImage", e);
      return null;
    }
  }

  public File getScreenShotAsFile() {
    if (!WebDriverRunner.hasWebDriverStarted()) {
      log.warning("Cannot take screenshot because browser is not started");
      return null;
    }

    WebDriver webdriver = getWebDriver();
    //File pageSource = savePageSourceToFile(fileName, webdriver); - temporary not available
    File scrFile = getPageImage(webdriver);

    String screenshot = scrFile.getAbsolutePath();
    addToHistory(screenshot);

    return scrFile;
  }

  protected File savePageImageToFile(String fileName, WebDriver webdriver) {
    File imageFile = null;
    if (webdriver instanceof TakesScreenshot) {
      imageFile = takeScreenshotImage((TakesScreenshot) webdriver, fileName);
    } else if (webdriver instanceof RemoteWebDriver) { // TODO Remove this obsolete branch
      WebDriver remoteDriver = new Augmenter().augment(webdriver);
      if (remoteDriver instanceof TakesScreenshot) {
        imageFile = takeScreenshotImage((TakesScreenshot) remoteDriver, fileName);
      }
    }
    return imageFile;
  }

  protected File getPageImage(WebDriver webdriver) {
    File scrFile = null;
    if (webdriver instanceof TakesScreenshot) {
      scrFile = takeScreenshotInMemory((TakesScreenshot) webdriver);
    } else if (webdriver instanceof RemoteWebDriver) { // TODO Remove this obsolete branch
      WebDriver remoteDriver = new Augmenter().augment(webdriver);
      if (remoteDriver instanceof TakesScreenshot) {
        scrFile = takeScreenshotInMemory((TakesScreenshot) remoteDriver);
      }
    }
    return scrFile;
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
          log.severe(e + ": " + alert.getText());
          alert.accept();
          savePageSourceToFile(fileName, webdriver, false);
        }
        catch (Exception unableToCloseAlert) {
          log.severe("Failed to close alert: " + unableToCloseAlert);
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

  protected File takeScreenshotInMemory(TakesScreenshot driver) {
    try {
      return driver.getScreenshotAs(FILE);
    } catch (Exception e) {
      printOnce("takeScreenshotAsFile", e);
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
      log.log(SEVERE, "Failed to write file " + targetFile.getAbsolutePath(), e);
    }
  }

  protected File ensureFolderExists(File targetFile) {
    File folder = targetFile.getParentFile();
    if (!folder.exists()) {
      log.info("Creating folder: " + folder);
      if (!folder.mkdirs()) {
        log.severe("Failed to create " + folder);
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
