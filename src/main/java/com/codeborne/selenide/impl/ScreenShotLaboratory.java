package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideTargetLocator;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.regex.Pattern;

import static java.io.File.separatorChar;
import static java.lang.ThreadLocal.withInitial;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static org.openqa.selenium.OutputType.FILE;

@ParametersAreNonnullByDefault
public class ScreenShotLaboratory {
  private static final Logger log = LoggerFactory.getLogger(ScreenShotLaboratory.class);

  private static final ScreenShotLaboratory instance = new ScreenShotLaboratory();
  private static final Pattern REGEX_PLUS = Pattern.compile("\\+");

  public static ScreenShotLaboratory getInstance() {
    return instance;
  }

  protected final List<File> allScreenshots = new ArrayList<>();
  protected AtomicLong screenshotCounter = new AtomicLong();
  protected ThreadLocal<String> currentContext = withInitial(() -> "");
  protected ThreadLocal<List<File>> currentContextScreenshots = new ThreadLocal<>();
  protected ThreadLocal<List<File>> threadScreenshots = withInitial(ArrayList::new);

  protected ScreenShotLaboratory() {
  }

  @Nullable
  public String takeScreenShot(Driver driver, String className, String methodName) {
    return takeScreenShot(driver, getScreenshotFileName(className, methodName));
  }

  @Nonnull
  protected String getScreenshotFileName(String className, String methodName) {
    return className.replace('.', separatorChar) + separatorChar +
      methodName + '.' + timestamp();
  }

  @Nullable
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
  @Nullable
  public String takeScreenShot(Driver driver, String fileName) {
    return ifWebDriverStarted(driver, webDriver ->
      ifReportsFolderNotNull(driver.config(), config ->
        takeScreenShot(config, webDriver, fileName)));
  }

  @Nullable
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

  @Nullable
  public File takeScreenshot(Driver driver, WebElement element) {
    try {
      BufferedImage destination = takeScreenshotAsImage(driver, element);
      if (destination != null) {
        return writeToFile(driver, destination);
      }
    } catch (IOException e) {
      log.error("Failed to take screenshot of {}", element, e);
    }
    return null;
  }

  @Nullable
  public BufferedImage takeScreenshotAsImage(Driver driver, WebElement element) {
    return ifWebDriverStarted(driver, webdriver ->
      ifReportsFolderNotNull(driver.config(), config ->
        takeScreenshotAsImage(webdriver, element)));
  }

  @Nullable
  private BufferedImage takeScreenshotAsImage(WebDriver webdriver, WebElement element) {
    if (!(webdriver instanceof TakesScreenshot)) {
      log.warn("Cannot take screenshot because browser does not support screenshots");
      return null;
    }

    byte[] screen = ((TakesScreenshot) webdriver).getScreenshotAs(OutputType.BYTES);

    Point elementLocation = element.getLocation();
    try {
      BufferedImage img = ImageIO.read(new ByteArrayInputStream(screen));
      int elementWidth = getRescaledElementWidth(element, img);
      int elementHeight = getRescaledElementHeight(element, img);

      return img.getSubimage(elementLocation.getX(), elementLocation.getY(), elementWidth, elementHeight);
    } catch (IOException e) {
      log.error("Failed to take screenshot of {}", element, e);
      return null;
    } catch (RasterFormatException e) {
      log.warn("Cannot take screenshot because element is not displayed on current screen position");
      return null;
    }
  }

  @Nonnull
  protected String generateScreenshotFileName() {
    return currentContext.get() + timestamp() + "." + screenshotCounter.getAndIncrement();
  }

  protected void ensureFolderExists(File targetFile) {
    File folder = targetFile.getParentFile();
    if (!folder.exists()) {
      log.info("Creating folder: {}", folder);
      if (!folder.mkdirs()) {
        log.error("Failed to create {}", folder);
      }
    }
  }

  protected long timestamp() {
    return System.currentTimeMillis();
  }

  @Nullable
  public File takeScreenshot(Driver driver, WebElement iframe, WebElement element) {
    try {
      BufferedImage destination = takeScreenshotAsImage(driver, iframe, element);
      if (destination != null) {
        return writeToFile(driver, destination);
      }
    } catch (IOException e) {
      log.error("Failed to take screenshot of {} inside frame {}", element, iframe, e);
    }
    return null;
  }

  @Nonnull
  private File writeToFile(Driver driver, BufferedImage destination) throws IOException {
    File screenshotOfElement = new File(driver.config().reportsFolder(), generateScreenshotFileName() + ".png");
    ensureFolderExists(screenshotOfElement);
    ImageIO.write(destination, "png", screenshotOfElement);
    return screenshotOfElement;
  }

  @Nullable
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
    } catch (IOException e) {
      log.error("Failed to take screenshot of {} inside frame {}", element, iframe, e);
      return null;
    } catch (RasterFormatException ex) {
      log.warn("Cannot take screenshot because iframe is not displayed");
      return null;
    }
    int iframeHeight = getRescaledElementHeight(iframe, img);
    SelenideTargetLocator switchTo = new SelenideTargetLocator(driver.config(), driver.getWebDriver());
    switchTo.frame(iframe);
    int iframeWidth = getRescaledIframeWidth(iframe, img, webdriver);

    Point elementLocation = element.getLocation();
    int elementWidth = getRescaledElementWidth(element, iframeWidth);
    int elementHeight = getRescaledElementHeight(element, iframeHeight);
    switchTo.defaultContent();
    try {
      img = img.getSubimage(iframeLocation.getX() + elementLocation.getX(), iframeLocation.getY() + elementLocation.getY(),
        elementWidth, elementHeight);
    } catch (RasterFormatException ex) {
      log.warn("Cannot take screenshot because element is not displayed in iframe");
      return null;
    }
    return img;
  }

  @Nullable
  private WebDriver checkIfFullyValidDriver(Driver driver) {
    return ifWebDriverStarted(driver, this::checkIfFullyValidDriver);
  }

  @Nullable
  private WebDriver checkIfFullyValidDriver(WebDriver webdriver) {
    if (!(webdriver instanceof TakesScreenshot)) {
      log.warn("Cannot take screenshot because browser does not support screenshots");
      return null;
    } else if (!(webdriver instanceof JavascriptExecutor)) {
      log.warn("Cannot take screenshot as driver is not supporting javascript execution");
      return null;
    }
    return webdriver;
  }

  @Nullable
  public File takeScreenShotAsFile(Driver driver) {
    return ifWebDriverStarted(driver, this::takeScreenShotAsFile);
  }

  @Nullable
  private File takeScreenShotAsFile(WebDriver webdriver) {
    //File pageSource = savePageSourceToFile(fileName, webdriver); - temporary not available
    File scrFile = getPageImage(webdriver);
    if (scrFile != null) {
      addToHistory(scrFile);
    }
    return scrFile;
  }

  @Nullable
  protected File getPageImage(WebDriver webdriver) {
    return webdriver instanceof TakesScreenshot ? takeScreenshotInMemory((TakesScreenshot) webdriver) : null;
  }

  @Nonnull
  protected File addToHistory(File screenshot) {
    if (currentContextScreenshots.get() != null) {
      currentContextScreenshots.get().add(screenshot);
    }
    synchronized (allScreenshots) {
      allScreenshots.add(screenshot);
    }
    threadScreenshots.get().add(screenshot);
    return screenshot;
  }

  @Nullable
  protected File takeScreenshotInMemory(TakesScreenshot driver) {
    try {
      return driver.getScreenshotAs(FILE);
    } catch (Exception e) {
      log.error("Failed to take screenshot in memory", e);
      return null;
    }
  }

  @Nullable
  protected File savePageImageToFile(Config config, String fileName, WebDriver webdriver) {
    if (webdriver instanceof TakesScreenshot) {
      return takeScreenshotImage(config, (TakesScreenshot) webdriver, fileName);
    }
    return null;
  }

  @Nonnull
  protected File savePageSourceToFile(Config config, String fileName, WebDriver webdriver) {
    return new PageSourceExtractor(config, webdriver, fileName).extract(true);
  }

  @Nullable
  protected File takeScreenshotImage(Config config, TakesScreenshot driver, String fileName) {
    try {
      File scrFile = driver.getScreenshotAs(FILE);
      File imageFile = new File(config.reportsFolder(), fileName + ".png");
      try {
        FileHelper.copyFile(scrFile, imageFile);
      } catch (IOException e) {
        log.error("Failed to save screenshot to {}", imageFile, e);
      }
      return imageFile;
    } catch (WebDriverException e) {
      log.error("Failed to take screenshot to {}", fileName, e);
      return null;
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

  @Nonnull
  public List<File> finishContext() {
    List<File> result = currentContextScreenshots.get();
    currentContext.set("");
    currentContextScreenshots.remove();
    return result;
  }

  @Nonnull
  public List<File> getScreenshots() {
    synchronized (allScreenshots) {
      return unmodifiableList(allScreenshots);
    }
  }

  @Nonnull
  public List<File> getThreadScreenshots() {
    List<File> screenshots = threadScreenshots.get();
    return screenshots == null ? emptyList() : unmodifiableList(screenshots);
  }

  @Nonnull
  public List<File> getContextScreenshots() {
    List<File> screenshots = currentContextScreenshots.get();
    return screenshots == null ? emptyList() : unmodifiableList(screenshots);
  }

  @Nullable
  public File getLastScreenshot() {
    synchronized (allScreenshots) {
      return allScreenshots.isEmpty() ? null : allScreenshots.get(allScreenshots.size() - 1);
    }
  }

  @Nonnull
  public Optional<File> getLastThreadScreenshot() {
    List<File> screenshots = threadScreenshots.get();
    return getLastScreenshot(screenshots);
  }

  @Nonnull
  public Optional<File> getLastContextScreenshot() {
    List<File> screenshots = currentContextScreenshots.get();
    return getLastScreenshot(screenshots);
  }

  @Nonnull
  private Optional<File> getLastScreenshot(@Nullable List<File> screenshots) {
    return screenshots == null || screenshots.isEmpty()
      ? Optional.empty()
      : Optional.of(screenshots.get(screenshots.size() - 1));
  }

  @Nonnull
  public String formatScreenShotPath(Driver driver) {
    if (!driver.config().screenshots()) {
      log.debug("Automatic screenshots are disabled.");
      return "";
    }

    String screenshot = takeScreenShot(driver);
    if (screenshot == null) {
      return "";
    }

    if (driver.config().reportsUrl() != null) {
      String screenShotURL = formatScreenShotURL(driver.config().reportsUrl(), screenshot);
      log.info("Replaced screenshot file path '{}' by public CI URL '{}'", screenshot, screenShotURL);
      return screenShotURL;
    }

    log.debug("reportsUrl is not configured. Returning screenshot file name '{}'", screenshot);
    try {
      return new File(screenshot).toURI().toURL().toExternalForm();
    } catch (MalformedURLException e) {
      return "file://" + screenshot;
    }
  }

  @Nonnull
  private String formatScreenShotURL(String reportsURL, String screenshot) {
    Path current = Paths.get(System.getProperty("user.dir"));
    Path target = Paths.get(screenshot).normalize();
    String screenShotPath;
    if (isInsideFolder(current, target)) {
      screenShotPath = current.relativize(target).toString().replace('\\', '/');
    } else {
      screenShotPath = target.toFile().getName();
    }
    return normalizeURL(reportsURL, screenShotPath);
  }

  @Nonnull
  private String normalizeURL(String reportsURL, String path) {
    return appendSlash(reportsURL) + encodePath(path);
  }

  @Nonnull
  private String appendSlash(String url) {
    return url.endsWith("/") ? url : url + "/";
  }

  @Nonnull
  String encodePath(String path) {
    return REGEX_PLUS.matcher(Arrays.stream(path.split("/"))
      .map(this::encode)
      .collect(joining("/"))).replaceAll("%20");
  }

  @Nonnull
  private String encode(String str) {
    try {
      return URLEncoder.encode(str, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      log.debug("Cannot encode path segment: {}", str, e);
      return str;
    }
  }

  private static boolean isInsideFolder(Path root, Path other) {
    return other.startsWith(root.toAbsolutePath());
  }

  @Nullable
  private <T> T ifWebDriverStarted(Driver driver, Function<WebDriver, T> lambda) {
    if (!driver.hasWebDriverStarted()) {
      log.warn("Cannot take screenshot because browser is not started");
      return null;
    }
    return lambda.apply(driver.getWebDriver());
  }

  @Nullable
  private <T> T ifReportsFolderNotNull(Config config, Function<Config, T> lambda) {
    if (config.reportsFolder() == null) {
      log.error("Cannot take screenshot because reportsFolder is null");
      return null;
    }
    return lambda.apply(config);
  }

  private int getRescaledElementWidth(WebElement element, int iframeWidth) {
    int elementWidth = getElementWidth(element);
    if (elementWidth > iframeWidth) {
      return iframeWidth - element.getLocation().getX();
    } else {
      return elementWidth;
    }
  }

  private int getRescaledElementHeight(WebElement element, int iframeHeight) {
    int elementHeight = getElementHeight(element);
    if (elementHeight > iframeHeight) {
      return iframeHeight - element.getLocation().getY();
    } else {
      return elementHeight;
    }
  }

  private int getRescaledElementWidth(WebElement element, BufferedImage image) {
    if (getElementWidth(element) > image.getWidth()) {
      return image.getWidth() - element.getLocation().getX();
    } else {
      return getElementWidth(element);
    }
  }

  private int getRescaledElementHeight(WebElement element, BufferedImage image) {
    if (getElementHeight(element) > image.getHeight()) {
      return image.getHeight() - element.getLocation().getY();
    } else {
      return getElementHeight(element);
    }
  }

  private int getRescaledIframeWidth(WebElement iframe, BufferedImage image, WebDriver driver) {
    if (getIframeWidth(driver) > image.getWidth()) {
      return image.getWidth() - iframe.getLocation().getX();
    } else {
      return getIframeWidth(driver);
    }
  }

  private int getIframeWidth(WebDriver driver) {
    return ((Long) ((JavascriptExecutor) driver).executeScript("return document.body.clientWidth")).intValue();
  }

  private int getElementWidth(WebElement element) {
    return element.getSize().getWidth();
  }

  private int getElementHeight(WebElement element) {
    return element.getSize().getHeight();
  }
   // Add methods for working with current context and screenshots without remove it.
   public File getCurrentThreadLatestScreenshot() {
    List<File> result = currentContextScreenshots.get();
    return result == null ? null : result.get(result.size() - 1);
  }

  public List<File> getCurrentThreadAllScreenshots() {
    List<File> allScreenshots = currentContextScreenshots.get();
    return allScreenshots == null ? null : Collections.unmodifiableList(allScreenshots);
  }
}
