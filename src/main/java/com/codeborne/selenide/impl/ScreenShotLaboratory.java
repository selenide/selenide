package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.reportsFolder;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.io.File.separatorChar;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import static org.openqa.selenium.OutputType.FILE;

public class ScreenShotLaboratory {
  private static final Logger log = Logger.getLogger(ScreenShotLaboratory.class.getName());
  protected final List<File> allScreenshots = new ArrayList<>();
  protected AtomicLong screenshotCounter = new AtomicLong();
  protected ThreadLocal<String> currentContext = ThreadLocal.withInitial(() -> "");
  protected ThreadLocal<List<File>> currentContextScreenshots = new ThreadLocal<>();
  protected Set<String> printedErrors = new ConcurrentSkipListSet<>();

  public String takeScreenShot(String className, String methodName) {
    return takeScreenShot(getScreenshotFileName(className, methodName));
  }

  protected String getScreenshotFileName(String className, String methodName) {
    return className.replace('.', separatorChar) + separatorChar +
      methodName + '.' + timestamp();
  }

  public String takeScreenShot() {
    return takeScreenShot(generateScreenshotFileName());
  }

  /**
   * Takes screenshot of current browser window.
   * Stores 2 files: html of page (if "savePageSource" option is enabled), and (if possible) image in PNG format.
   *
   * @param fileName name of file (without extension) to store screenshot to.
   *
   * @return the name of last saved screenshot or null if failed to create screenshot
   */
  public String takeScreenShot(String fileName) {
    if (!WebDriverRunner.hasWebDriverStarted()) {
      log.warning("Cannot take screenshot because browser is not started");
      return null;
    }
    else if (reportsFolder == null) {
      log.severe("Cannot take screenshot because Configuration.reportsFolder is null");
      return null;
    }

    WebDriver webdriver = getWebDriver();

    File screenshot = null;
    if (Configuration.savePageSource) {
      screenshot = savePageSourceToFile(fileName, webdriver);
    }

    File imageFile = savePageImageToFile(fileName, webdriver);
    if (imageFile != null) {
      screenshot = imageFile;
    }
    if (screenshot == null) {
      return null;
    }
    return addToHistory(screenshot).getAbsolutePath();
  }

  public File takeScreenshot(WebElement element) {
    try {
      BufferedImage destination = takeScreenshotAsImage(element);
      if (destination == null) {
        return null;
      }
      File screenshotOfElement = new File(reportsFolder, generateScreenshotFileName() + ".png");
      ensureFolderExists(screenshotOfElement);
      ImageIO.write(destination, "png", screenshotOfElement);
      return screenshotOfElement;
    }
    catch (IOException e) {
      log.log(SEVERE, "Failed to take screenshot of " + element, e);
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

    Point elementLocation = element.getLocation();
    try {
      BufferedImage img = ImageIO.read(new ByteArrayInputStream(screen));
      int elementWidth = element.getSize().getWidth();
      int elementHeight = element.getSize().getHeight();
      if (elementWidth > img.getWidth()) {
        elementWidth = img.getWidth() - elementLocation.getX();
      }
      if (elementHeight > img.getHeight()) {
        elementHeight = img.getHeight() - elementLocation.getY();
      }
      return img.getSubimage(elementLocation.getX(), elementLocation.getY(), elementWidth, elementHeight);
    }
    catch (IOException e) {
      log.log(SEVERE, "Failed to take screenshot of " + element, e);
      return null;
    }
    catch (RasterFormatException e) {
      log.warning("Cannot take screenshot because element is not displayed on current screen position");
      return null;
    }
  }

  protected String generateScreenshotFileName() {
    return currentContext.get() + timestamp() + "." + screenshotCounter.getAndIncrement();
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

  protected synchronized void printOnce(String action, Throwable error) {
    if (!printedErrors.contains(action)) {
      log.log(SEVERE, error.getMessage(), error);
      printedErrors.add(action);
    } else {
      log.severe("Failed to " + action + ": " + error);
    }
  }

  protected long timestamp() {
    return System.currentTimeMillis();
  }

  public File takeScreenshot(WebElement iframe, WebElement element) {
    try {
      BufferedImage dest = takeScreenshotAsImage(iframe, element);
      if (dest == null) {
        return null;
      }
      File screenshotOfElement = new File(reportsFolder, generateScreenshotFileName() + ".png");
      ensureFolderExists(screenshotOfElement);
      ImageIO.write(dest, "png", screenshotOfElement);
      return screenshotOfElement;
    }
    catch (IOException e) {
      log.log(SEVERE, "Failed to take screenshot of " + element + " inside frame " + iframe, e);
      return null;
    }
  }

  public BufferedImage takeScreenshotAsImage(WebElement iframe, WebElement element) {
    WebDriver webdriver = checkIfFullyValidDriver();
    if (webdriver == null) {
      return null;
    }
    byte[] screen = ((TakesScreenshot) webdriver).getScreenshotAs(OutputType.BYTES);
    Point iframeLocation = iframe.getLocation();
    BufferedImage img;
    try {
      img = ImageIO.read(new ByteArrayInputStream(screen));
    }
    catch (IOException e) {
      log.log(SEVERE, "Failed to take screenshot of " + element + " inside frame " + iframe, e);
      return null;
    }
    catch (RasterFormatException ex) {
      log.warning("Cannot take screenshot because iframe is not displayed");
      return null;
    }
    int iframeHeight = iframe.getSize().getHeight();
    switchTo().frame(iframe);
    int iframeWidth = ((Long) ((JavascriptExecutor) webdriver).executeScript("return document.body.clientWidth")).intValue();
    if (iframeHeight > img.getHeight()) {
      iframeHeight = img.getHeight() - iframeLocation.getY();
    }
    if (iframeWidth > img.getWidth()) {
      iframeWidth = img.getWidth() - iframeLocation.getX();
    }
    Point elementLocation = element.getLocation();
    int elementWidth = element.getSize().getWidth();
    int elementHeight = element.getSize().getHeight();
    if (elementWidth > iframeWidth) {
      elementWidth = iframeWidth - elementLocation.getX();
    }
    if (elementHeight > iframeHeight) {
      elementHeight = iframeHeight - elementLocation.getY();
    }
    switchTo().defaultContent();
    try {
      img = img.getSubimage(iframeLocation.getX() + elementLocation.getX(), iframeLocation.getY() + elementLocation.getY(),
        elementWidth, elementHeight);
    }
    catch (RasterFormatException ex) {
      log.warning("Cannot take screenshot because element is not displayed in iframe");
      return null;
    }
    return img;
  }

  private WebDriver checkIfFullyValidDriver() {
    if (!WebDriverRunner.hasWebDriverStarted()) {
      log.warning("Cannot take screenshot because browser is not started");
      return null;
    }

    WebDriver webdriver = getWebDriver();
    if (!(webdriver instanceof TakesScreenshot)) {
      log.warning("Cannot take screenshot because browser does not support screenshots");
      return null;
    } else if (!(webdriver instanceof JavascriptExecutor)) {
      log.warning("Cannot take screenshot as driver is not supporting javascript execution");
      return null;
    }
    return webdriver;
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

  protected File getPageImage(WebDriver webdriver) {
    File scrFile = null;
    if (webdriver instanceof TakesScreenshot) {
      scrFile = takeScreenshotInMemory((TakesScreenshot) webdriver);
    }
    return scrFile;
  }

  protected File addToHistory(File screenshot) {
    if (currentContextScreenshots.get() != null) {
      currentContextScreenshots.get().add(screenshot);
    }
    synchronized (allScreenshots) {
      allScreenshots.add(screenshot);
    }
    return screenshot;
  }

  protected File takeScreenshotInMemory(TakesScreenshot driver) {
    try {
      return driver.getScreenshotAs(FILE);
    }
    catch (Exception e) {
      log.log(SEVERE, "Failed to take screenshot in memory", e);
      return null;
    }
  }

  protected File savePageImageToFile(String fileName, WebDriver webdriver) {
    File imageFile = null;
    if (webdriver instanceof TakesScreenshot) {
      imageFile = takeScreenshotImage((TakesScreenshot) webdriver, fileName);
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
          log.severe(e + ": " + alert.getText());
          alert.accept();
          savePageSourceToFile(fileName, webdriver, false);
        } catch (Exception unableToCloseAlert) {
          log.severe("Failed to close alert: " + unableToCloseAlert);
        }
      } else {
        printOnce("savePageSourceToFile", e);
      }
    } catch (WebDriverException e) {
      log.log(WARNING, "Failed to save page source to " + fileName + " because of " + e);
      writeToFile(e.toString(), pageSource);
      return pageSource;
    } catch (RuntimeException e) {
      log.log(SEVERE, "Failed to save page source to " + fileName, e);
      writeToFile(e.toString(), pageSource);
    }
    return pageSource;
  }

  protected File takeScreenshotImage(TakesScreenshot driver, String fileName) {
    try {
      File scrFile = driver.getScreenshotAs(FILE);
      File imageFile = new File(reportsFolder, fileName + ".png");
      try {
        copyFile(scrFile, imageFile);
      }
      catch (IOException e) {
        log.log(SEVERE, "Failed to save screenshot to " + imageFile, e);
      }
      return imageFile;
    }
    catch (WebDriverException e) {
      log.log(SEVERE, "Failed to take screenshot to " + fileName + " because of " + e);
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
    try (ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes(UTF_8))) {
      copyFile(in, targetFile);
    }
    catch (IOException e) {
      log.log(SEVERE, "Failed to write file " + targetFile.getAbsolutePath(), e);
    }
  }

  public void startContext(String className, String methodName) {
    String context = className.replace('.', separatorChar) + separatorChar + methodName + separatorChar;
    startContext(context);
  }

  public void startContext(String context) {
    currentContext.set(context);
    currentContextScreenshots.set(new ArrayList<>());
  }

  public List<File> finishContext() {
    List<File> result = currentContextScreenshots.get();
    currentContext.set("");
    currentContextScreenshots.remove();
    return result;
  }

  public List<File> getScreenshots() {
    synchronized (allScreenshots) {
      return Collections.unmodifiableList(allScreenshots);
    }
  }

  public File getLastScreenshot() {
    synchronized (allScreenshots) {
      return allScreenshots.isEmpty() ? null : allScreenshots.get(allScreenshots.size() - 1);
    }
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
      } catch (MalformedURLException ignore) {
        // ignored exception
      }
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
