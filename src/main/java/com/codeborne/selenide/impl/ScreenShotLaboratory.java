package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.reportsFolder;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.io.File.separatorChar;
import static java.util.logging.Level.SEVERE;
import static org.openqa.selenium.OutputType.FILE;

public class ScreenShotLaboratory {
  private static final Logger log = Logger.getLogger(ScreenShotLaboratory.class.getName());

  protected AtomicLong screenshotCounter = new AtomicLong();
  protected String currentContext = "";
  protected List<File> currentContextScreenshots;
  protected List<File> allScreenshots = new ArrayList<>();

  protected Set<String> printedErrors = new ConcurrentSkipListSet<>();

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
   * Stores 2 files: html of page (if "savePageSource" option is enabled), and (if possible) image in PNG format.
   * 
   * @param fileName name of file (without extension) to store screenshot to.
   * @return the name of last saved screenshot or null if failed to create screenshot
   */
  public String takeScreenShot(String fileName) {
    if (!WebDriverRunner.hasWebDriverStarted()) {
      log.warning("Cannot take screenshot because browser is not started");
      return null;
    }

    WebDriver webdriver = getWebDriver();

    if (Configuration.savePageSource) {
      savePageSourceToFile(fileName, webdriver);
    }

    File imageFile = savePageImageToFile(fileName, webdriver);
    if (imageFile == null) {
      return null;
    }
    return addToHistory(imageFile).getAbsolutePath();
  }

  public File takeScreenshot(WebElement element) {
    try {
      BufferedImage dest = takeScreenshotAsImage(element);
      File screenshotOfElement = new File(reportsFolder, generateScreenshotFileName() + ".png");
      ImageIO.write(dest, "png", screenshotOfElement);
      return screenshotOfElement;
    }
    catch (IOException e) {
      printOnce("takeScreenshot", e);
      return null;
    }
  }

  public BufferedImage takeScreenshotAsImage(WebElement element) {
    if (!WebDriverRunner.hasWebDriverStarted()) {
      log.warning("Cannot take screenshot because browser is not started");
      return null;
    }

    WebDriver webdriver = getWebDriver();
    if (!(webdriver instanceof TakesScreenshot)) {
      log.warning("Cannot take screenshot because browser does not support screenshots");
      return null;
    }

    byte[] screen = ((TakesScreenshot) webdriver).getScreenshotAs(OutputType.BYTES);

    Point p = element.getLocation();
    Dimension elementSize = element.getSize();

    try {
      BufferedImage img = ImageIO.read(new ByteArrayInputStream(screen));
      BufferedImage dest = img.getSubimage(p.getX(), p.getY(), elementSize.getWidth(), elementSize.getHeight());
      return dest;
    }
    catch (IOException e) {
      printOnce("takeScreenshotImage", e);
      return null;
    }
  }

  public File takeScreenShotAsFile() {
    if (!WebDriverRunner.hasWebDriverStarted()) {
      log.warning("Cannot take screenshot because browser is not started");
      return null;
    }

    WebDriver webdriver = getWebDriver();
    //File pageSource = savePageSourceToFile(fileName, webdriver); - temporary not available
    File scrFile = getPageImage(webdriver);
    addToHistory(scrFile);
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

  protected File addToHistory(File screenshot) {
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
    try (FileInputStream in = new FileInputStream(sourceFile)) {
      copyFile(in, targetFile);
    }
  }

  protected void copyFile(InputStream in, File targetFile) throws IOException {
    ensureFolderExists(targetFile);

    try (FileOutputStream out = new FileOutputStream(targetFile)) {
      byte[] buffer = new byte[1024];
      int len;
      while ((len = in.read(buffer)) != -1) {
        out.write(buffer, 0, len);
      }
    }
  }

  protected void writeToFile(String content, File targetFile) {
    try (ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"))) {
      copyFile(in, targetFile);
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
    currentContextScreenshots = new ArrayList<>();
  }

  public List<File> finishContext() {
    List<File> result = currentContextScreenshots;
    this.currentContext = "";
    currentContextScreenshots = null;
    return result;
  }

  public List<File> getScreenshots() {
    return allScreenshots;
  }
  
  public File getLastScreenshot() {
    return allScreenshots.isEmpty() ? null : allScreenshots.get(allScreenshots.size() - 1);
  }

  public String formatScreenShotPath() {
    if (!Configuration.screenshots) {
      log.config("Automatic screenshots are disabled.");
      return "";
    }

    String screenshot = takeScreenShot();
    if (screenshot == null) {
      return "";
    }

    if (Configuration.reportsUrl != null) {
      String screenshotRelativePath = screenshot.substring(System.getProperty("user.dir").length() + 1);
      String screenshotUrl = Configuration.reportsUrl + screenshotRelativePath.replace('\\', '/');
      try {
        screenshotUrl = new URL(screenshotUrl).toExternalForm();
      }
      catch (MalformedURLException ignore) { }
      log.config("Replaced screenshot file path '" + screenshot + "' by public CI URL '" + screenshotUrl + "'");
      return screenshotUrl;
    }

    log.config("reportsUrl is not configured. Returning screenshot file name '" + screenshot + "'");
    try {
      return new File(screenshot).toURI().toURL().toExternalForm();
    } catch (MalformedURLException e) {
      return "file://" + screenshot;
    }
  }
}
