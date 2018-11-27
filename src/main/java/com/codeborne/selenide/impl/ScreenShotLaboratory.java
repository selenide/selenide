package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideTargetLocator;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.logging.Logger;

import static java.io.File.separatorChar;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import static org.openqa.selenium.OutputType.FILE;

public class ScreenShotLaboratory {
  private static final Logger log = Logger.getLogger(ScreenShotLaboratory.class.getName());
  private static final ScreenShotLaboratory instance = new ScreenShotLaboratory();

  public static ScreenShotLaboratory getInstance() {
    return instance;
  }

  protected final List<File> allScreenshots = new ArrayList<>();
  protected AtomicLong screenshotCounter = new AtomicLong();
  protected ThreadLocal<String> currentContext = ThreadLocal.withInitial(() -> "");
  protected ThreadLocal<List<File>> currentContextScreenshots = new ThreadLocal<>();
  protected Set<String> printedErrors = new ConcurrentSkipListSet<>();

  public String takeScreenShot(Driver driver, String className, String methodName) {
    return takeScreenShot(driver, getScreenshotFileName(className, methodName));
  }

  protected String getScreenshotFileName(String className, String methodName) {
    return className.replace('.', separatorChar) + separatorChar +
      methodName + '.' + timestamp();
  }

  public String takeScreenShot(Driver driver) {
    return takeScreenShot(driver, generateScreenshotFileName());
  }

  /**
   * Takes screenshot of current browser window.
   * Stores 2 files: html of page (if "savePageSource" option is enabled), and (if possible) image in PNG format.
   *
   * @param fileName name of file (without extension) to store screenshot to.
   * @return the name of last saved screenshot or null if failed to create screenshot
   */
  public String takeScreenShot(Driver driver, String fileName) {
    return ifWebDriverStarted(driver, webDriver ->
      ifReportsFolderNotNull(driver.config(), config ->
        takeScreenShot(config, webDriver, fileName)));
  }

  private String takeScreenShot(Config config, WebDriver webDriver, String fileName) {
    File screenshot = null;
    if (config.savePageSource()) {
      screenshot = savePageSourceToFile(config, fileName, webDriver);
    }

    File imageFile = savePageImageToFile(config, fileName, webDriver);
    if (imageFile != null) {
      screenshot = imageFile;
    }
    if (screenshot == null) {
      return null;
    }
    return addToHistory(screenshot).getAbsolutePath();
  }

  public File takeScreenshot(Driver driver, WebElement element) {
    try {
      BufferedImage destination = takeScreenshotAsImage(driver, element);
      return writeToFile(driver, destination);
    }
    catch (IOException e) {
      log.log(SEVERE, "Failed to take screenshot of " + element, e);
      return null;
    }
  }

  public BufferedImage takeScreenshotAsImage(Driver driver, WebElement element) {
    return ifWebDriverStarted(driver, webdriver ->
      ifReportsFolderNotNull(driver.config(), config ->
        takeScreenshotAsImage(webdriver, element)));
  }

  private BufferedImage takeScreenshotAsImage(WebDriver webdriver, WebElement element) {
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

  protected void ensureFolderExists(File targetFile) {
    File folder = targetFile.getParentFile();
    if (!folder.exists()) {
      log.info("Creating folder: " + folder);
      if (!folder.mkdirs()) {
        log.severe("Failed to create " + folder);
      }
    }
  }

  protected synchronized void printOnce(String action, Throwable error) {
    if (!printedErrors.contains(action)) {
      log.log(SEVERE, error.getMessage(), error);
      printedErrors.add(action);
    }
    else {
      log.severe("Failed to " + action + ": " + error);
    }
  }

  protected long timestamp() {
    return System.currentTimeMillis();
  }

  public File takeScreenshot(Driver driver, WebElement iframe, WebElement element) {
    try {
      BufferedImage dest = takeScreenshotAsImage(driver, iframe, element);
      return writeToFile(driver, dest);
    }
    catch (IOException e) {
      log.log(SEVERE, "Failed to take screenshot of " + element + " inside frame " + iframe, e);
      return null;
    }
  }

  private File writeToFile(Driver driver, BufferedImage dest) throws IOException {
    if (dest == null) {
      return null;
    }
    File screenshotOfElement = new File(driver.config().reportsFolder(), generateScreenshotFileName() + ".png");
    ensureFolderExists(screenshotOfElement);
    ImageIO.write(dest, "png", screenshotOfElement);
    return screenshotOfElement;
  }

  public BufferedImage takeScreenshotAsImage(Driver driver, WebElement iframe, WebElement element) {
    WebDriver webdriver = checkIfFullyValidDriver(driver);
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
    SelenideTargetLocator switchTo = new SelenideTargetLocator(driver.config(), driver.getWebDriver());
    switchTo.frame(iframe);
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
    switchTo.defaultContent();
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

  private WebDriver checkIfFullyValidDriver(Driver driver) {
    return ifWebDriverStarted(driver, this::checkIfFullyValidDriver);
  }

  private WebDriver checkIfFullyValidDriver(WebDriver webdriver) {
    if (!(webdriver instanceof TakesScreenshot)) {
      log.warning("Cannot take screenshot because browser does not support screenshots");
      return null;
    }
    else if (!(webdriver instanceof JavascriptExecutor)) {
      log.warning("Cannot take screenshot as driver is not supporting javascript execution");
      return null;
    }
    return webdriver;
  }

  public File takeScreenShotAsFile(Driver driver) {
    return ifWebDriverStarted(driver, this::takeScreenShotAsFile);
  }

  private File takeScreenShotAsFile(WebDriver webdriver) {
    //File pageSource = savePageSourceToFile(fileName, webdriver); - temporary not available
    File scrFile = getPageImage(webdriver);
    addToHistory(scrFile);
    return scrFile;
  }

  protected File getPageImage(WebDriver webdriver) {
    return webdriver instanceof TakesScreenshot ? takeScreenshotInMemory((TakesScreenshot) webdriver) : null;
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

  protected File savePageImageToFile(Config config, String fileName, WebDriver webdriver) {
    File imageFile = null;
    if (webdriver instanceof TakesScreenshot) {
      imageFile = takeScreenshotImage(config, (TakesScreenshot) webdriver, fileName);
    }
    return imageFile;
  }

  protected File savePageSourceToFile(Config config, String fileName, WebDriver webdriver) {
    return savePageSourceToFile(config, fileName, webdriver, true);
  }

  protected File savePageSourceToFile(Config config, String fileName, WebDriver webdriver, boolean retryIfAlert) {
    File pageSource = new File(config.reportsFolder(), fileName + ".html");

    try {
      writeToFile(webdriver.getPageSource(), pageSource);
    }
    catch (UnhandledAlertException e) {
      if (retryIfAlert) {
        try {
          Alert alert = webdriver.switchTo().alert();
          log.severe(e + ": " + alert.getText());
          alert.accept();
          savePageSourceToFile(config, fileName, webdriver, false);
        }
        catch (Exception unableToCloseAlert) {
          log.severe("Failed to close alert: " + unableToCloseAlert);
        }
      }
      else {
        printOnce("savePageSourceToFile", e);
      }
    }
    catch (WebDriverException e) {
      log.log(WARNING, "Failed to save page source to " + fileName + " because of " + e);
      writeToFile(e.toString(), pageSource);
      return pageSource;
    }
    catch (RuntimeException e) {
      log.log(SEVERE, "Failed to save page source to " + fileName, e);
      writeToFile(e.toString(), pageSource);
    }
    return pageSource;
  }

  protected File takeScreenshotImage(Config config, TakesScreenshot driver, String fileName) {
    try {
      File scrFile = driver.getScreenshotAs(FILE);
      File imageFile = new File(config.reportsFolder(), fileName + ".png");
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

  public String formatScreenShotPath(Driver driver) {
    if (!driver.config().screenshots()) {
      log.config("Automatic screenshots are disabled.");
      return "";
    }

    String screenshot = takeScreenShot(driver);
    if (screenshot == null) {
      return "";
    }

    if (driver.config().reportsUrl() != null) {
      Path current = Paths.get(System.getProperty("user.dir"));
      Path target = Paths.get(screenshot).normalize();
      String screenshotUrl;
      if (isInsideFolder(current, target)) {
        Path relativePath = current.relativize(target);
        screenshotUrl = driver.config().reportsUrl() + relativePath.toString().replace('\\', '/');
      } else {
        String name = target.toFile().getName();
        screenshotUrl = driver.config().reportsUrl() + name;
      }
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
    }
    catch (MalformedURLException e) {
      return "file://" + screenshot;
    }
  }

  private static boolean isInsideFolder(Path root, Path other) {
    return other.startsWith(root.toAbsolutePath());
  }

  private <T> T ifWebDriverStarted(Driver driver, Function<WebDriver, T> lambda) {
    if (!driver.hasWebDriverStarted()) {
      log.warning("Cannot take screenshot because browser is not started");
      return null;
    }
    return lambda.apply(driver.getWebDriver());
  }

  private <T> T ifReportsFolderNotNull(Config config, Function<Config, T> lambda) {
    if (config.reportsFolder() == null) {
      log.severe("Cannot take screenshot because reportsFolder is null");
      return null;
    }
    return lambda.apply(config);
  }
}
