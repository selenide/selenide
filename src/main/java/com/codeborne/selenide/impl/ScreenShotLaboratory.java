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

import javax.annotation.CheckReturnValue;
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

import static com.codeborne.selenide.impl.FileHelper.ensureParentFolderExists;
import static com.codeborne.selenide.impl.Plugins.inject;
import static com.codeborne.selenide.impl.WebElementUtils.unwrap;
import static java.io.File.separatorChar;
import static java.lang.ThreadLocal.withInitial;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static org.openqa.selenium.OutputType.BYTES;
import static org.openqa.selenium.OutputType.FILE;

@ParametersAreNonnullByDefault
public class ScreenShotLaboratory {
  private static final Logger log = LoggerFactory.getLogger(ScreenShotLaboratory.class);

  private static final ScreenShotLaboratory instance = new ScreenShotLaboratory();
  private static final Pattern REGEX_PLUS = Pattern.compile("\\+");

  public static ScreenShotLaboratory getInstance() {
    return instance;
  }

  private final Photographer photographer = inject(Photographer.class);
  private final PageSourceExtractor extractor = inject(PageSourceExtractor.class);
  protected final List<File> allScreenshots = new ArrayList<>();
  protected AtomicLong screenshotCounter = new AtomicLong();
  protected ThreadLocal<String> currentContext = withInitial(() -> "");
  protected ThreadLocal<List<File>> currentContextScreenshots = new ThreadLocal<>();
  protected ThreadLocal<List<File>> threadScreenshots = withInitial(ArrayList::new);

  protected ScreenShotLaboratory() {
  }

  @CheckReturnValue
  @Nullable
  public String takeScreenShot(Driver driver, String className, String methodName) {
    return takeScreenShot(driver, getScreenshotFileName(className, methodName));
  }

  @CheckReturnValue
  @Nonnull
  protected String getScreenshotFileName(String className, String methodName) {
    return className.replace('.', separatorChar) + separatorChar +
      methodName + '.' + timestamp();
  }

  @CheckReturnValue
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
  @CheckReturnValue
  @Nullable
  public String takeScreenShot(Driver driver, String fileName) {
    return ifWebDriverStarted(driver, webDriver ->
      ifReportsFolderNotNull(driver.config(), config ->
        takeScreenShot(config, driver, fileName)));
  }

  @CheckReturnValue
  @Nullable
  public <T> T takeScreenShot(Driver driver, OutputType<T> outputType) {
    return ifWebDriverStarted(driver, webDriver ->
      photographer.takeScreenshot(driver, outputType)
        .map(screenshot -> addToHistoryIfFile(screenshot, outputType))
        .orElse(null));
  }

  private <T> T addToHistoryIfFile(T screenshot, OutputType<T> outputType) {
    if (outputType == OutputType.FILE) {
      addToHistory((File) screenshot);
    }
    return screenshot;
  }

  @CheckReturnValue
  @Nullable
  private String takeScreenShot(Config config, Driver driver, String fileName) {
    File screenshot = null;
    if (config.savePageSource()) {
      screenshot = savePageSourceToFile(config, fileName, driver);
    }

    File imageFile = savePageImageToFile(config, fileName, driver);
    if (imageFile != null) {
      screenshot = imageFile;
    }
    if (screenshot == null) {
      return null;
    }
    return addToHistory(screenshot).getAbsolutePath();
  }

  @CheckReturnValue
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

  @CheckReturnValue
  @Nullable
  public BufferedImage takeScreenshotAsImage(Driver driver, WebElement element) {
    return ifWebDriverStarted(driver, webdriver ->
      ifReportsFolderNotNull(driver.config(), config ->
        takeElementScreenshotAsImage(driver, element).orElse(null)));
  }

  @CheckReturnValue
  @Nonnull
  private Optional<BufferedImage> takeElementScreenshotAsImage(Driver driver, WebElement element) {
    if (!(driver.getWebDriver() instanceof TakesScreenshot)) {
      log.warn("Cannot take screenshot because browser does not support screenshots");
      return Optional.empty();
    }

    return photographer.takeScreenshot(driver, BYTES)
      .flatMap(screen -> cropToElement(screen, element));
  }

  @CheckReturnValue
  @Nonnull
  private Optional<BufferedImage> cropToElement(byte[] screen, WebElement element) {
    Point elementLocation = element.getLocation();
    try {
      BufferedImage img = ImageIO.read(new ByteArrayInputStream(screen));
      int elementWidth = getRescaledElementWidth(element, img);
      int elementHeight = getRescaledElementHeight(element, img);

      return Optional.of(img.getSubimage(elementLocation.getX(), elementLocation.getY(), elementWidth, elementHeight));
    } catch (IOException e) {
      log.error("Failed to take screenshot of {}", element, e);
      return Optional.empty();
    } catch (RasterFormatException e) {
      log.warn("Cannot take screenshot because element is not displayed on current screen position");
      return Optional.empty();
    }
  }

  @CheckReturnValue
  @Nonnull
  protected String generateScreenshotFileName() {
    return currentContext.get() + timestamp() + "." + screenshotCounter.getAndIncrement();
  }

  protected long timestamp() {
    return System.currentTimeMillis();
  }

  @CheckReturnValue
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

  @CheckReturnValue
  @Nonnull
  private File writeToFile(Driver driver, BufferedImage destination) throws IOException {
    File screenshotOfElement = new File(driver.config().reportsFolder(), generateScreenshotFileName() + ".png");
    ensureParentFolderExists(screenshotOfElement);
    ImageIO.write(destination, "png", screenshotOfElement);
    return screenshotOfElement;
  }

  @CheckReturnValue
  @Nullable
  public BufferedImage takeScreenshotAsImage(Driver driver, WebElement iFrame, WebElement element) {
    WebElement iframe = unwrap(iFrame);

    WebDriver webdriver = checkIfFullyValidDriver(driver);
    if (webdriver == null) {
      return null;
    }

    Optional<byte[]> screenshot = photographer.takeScreenshot(driver, BYTES);

    return screenshot.flatMap(screen ->
      takeScreenshotAsImage(driver, iframe, element, screen))
      .orElse(null);
  }

  @CheckReturnValue
  @Nonnull
  private Optional<BufferedImage> takeScreenshotAsImage(Driver driver, WebElement iframe,
                                                        WebElement element, byte[] screen) {
    Point iframeLocation = iframe.getLocation();
    BufferedImage img;
    try {
      img = ImageIO.read(new ByteArrayInputStream(screen));
    } catch (IOException e) {
      log.error("Failed to take screenshot of {} inside frame {}", element, iframe, e);
      return Optional.empty();
    } catch (RasterFormatException ex) {
      log.warn("Cannot take screenshot because iframe is not displayed");
      return Optional.empty();
    }
    int iframeHeight = getRescaledElementHeight(iframe, img);
    SelenideTargetLocator switchTo = new SelenideTargetLocator(driver);
    switchTo.frame(iframe);
    int iframeWidth = getRescaledIframeWidth(iframe, img, driver.getWebDriver());

    Point elementLocation = element.getLocation();
    int elementWidth = getRescaledElementWidth(element, iframeWidth);
    int elementHeight = getRescaledElementHeight(element, iframeHeight);
    switchTo.defaultContent();
    try {
      img = img.getSubimage(iframeLocation.getX() + elementLocation.getX(), iframeLocation.getY() + elementLocation.getY(),
        elementWidth, elementHeight);
    } catch (RasterFormatException ex) {
      log.warn("Cannot take screenshot because element is not displayed in iframe");
      return Optional.empty();
    }
    return Optional.of(img);
  }

  @CheckReturnValue
  @Nullable
  private WebDriver checkIfFullyValidDriver(Driver driver) {
    return ifWebDriverStarted(driver, this::checkIfFullyValidDriver);
  }

  @CheckReturnValue
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

  @CheckReturnValue
  @Nullable
  public File takeScreenShotAsFile(Driver driver) {
    return ifWebDriverStarted(driver, webDriver -> {
      //File pageSource = savePageSourceToFile(fileName, webDriver); - temporary not available
      try {
        return photographer.takeScreenshot(driver, FILE)
          .map(this::addToHistory)
          .orElse(null);
      }
      catch (Exception e) {
        log.error("Failed to take screenshot in memory", e);
        return null;
      }
    });
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

  @CheckReturnValue
  @Nullable
  protected File savePageImageToFile(Config config, String fileName, Driver driver) {
    try {
      Optional<byte[]> scrFile = photographer.takeScreenshot(driver, BYTES);
      if (!scrFile.isPresent()) {
        log.info("Webdriver doesn't support screenshots");
        return null;
      }
      File imageFile = new File(config.reportsFolder(), fileName + ".png");
      try {
        FileHelper.writeToFile(scrFile.get(), imageFile);
      } catch (IOException e) {
        log.error("Failed to save screenshot to {}", imageFile, e);
      }
      return imageFile;
    } catch (WebDriverException e) {
      log.error("Failed to take screenshot to {}", fileName, e);
      return null;
    }
  }

  @CheckReturnValue
  @Nonnull
  protected File savePageSourceToFile(Config config, String fileName, Driver driver) {
    return extractor.extract(config, driver.getWebDriver(), fileName);
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

  @CheckReturnValue
  @Nonnull
  public List<File> getScreenshots() {
    synchronized (allScreenshots) {
      return unmodifiableList(allScreenshots);
    }
  }

  @CheckReturnValue
  @Nonnull
  public List<File> getThreadScreenshots() {
    List<File> screenshots = threadScreenshots.get();
    return screenshots == null ? emptyList() : unmodifiableList(screenshots);
  }

  @CheckReturnValue
  @Nonnull
  public List<File> getContextScreenshots() {
    List<File> screenshots = currentContextScreenshots.get();
    return screenshots == null ? emptyList() : unmodifiableList(screenshots);
  }

  @CheckReturnValue
  @Nullable
  public File getLastScreenshot() {
    synchronized (allScreenshots) {
      return allScreenshots.isEmpty() ? null : allScreenshots.get(allScreenshots.size() - 1);
    }
  }

  @CheckReturnValue
  @Nonnull
  public Optional<File> getLastThreadScreenshot() {
    List<File> screenshots = threadScreenshots.get();
    return getLastScreenshot(screenshots);
  }

  @CheckReturnValue
  @Nonnull
  public Optional<File> getLastContextScreenshot() {
    List<File> screenshots = currentContextScreenshots.get();
    return getLastScreenshot(screenshots);
  }

  @CheckReturnValue
  @Nonnull
  private Optional<File> getLastScreenshot(@Nullable List<File> screenshots) {
    return screenshots == null || screenshots.isEmpty()
      ? Optional.empty()
      : Optional.of(screenshots.get(screenshots.size() - 1));
  }

  @CheckReturnValue
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

  @CheckReturnValue
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

  @CheckReturnValue
  @Nonnull
  private String normalizeURL(String reportsURL, String path) {
    return appendSlash(reportsURL) + encodePath(path);
  }

  @CheckReturnValue
  @Nonnull
  private String appendSlash(String url) {
    return url.endsWith("/") ? url : url + "/";
  }

  @CheckReturnValue
  @Nonnull
  String encodePath(String path) {
    return REGEX_PLUS.matcher(Arrays.stream(path.split("/"))
      .map(this::encode)
      .collect(joining("/"))).replaceAll("%20");
  }

  @CheckReturnValue
  @Nonnull
  private String encode(String str) {
    try {
      return URLEncoder.encode(str, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      log.debug("Cannot encode path segment: {}", str, e);
      return str;
    }
  }

  @CheckReturnValue
  private static boolean isInsideFolder(Path root, Path other) {
    return other.startsWith(root.toAbsolutePath());
  }

  @CheckReturnValue
  @Nullable
  private <T> T ifWebDriverStarted(Driver driver, Function<WebDriver, T> lambda) {
    if (!driver.hasWebDriverStarted()) {
      log.warn("Cannot take screenshot because browser is not started");
      return null;
    }
    return lambda.apply(driver.getWebDriver());
  }

  @CheckReturnValue
  @Nullable
  private <T> T ifReportsFolderNotNull(Config config, Function<Config, T> lambda) {
    if (config.reportsFolder() == null) {
      log.error("Cannot take screenshot because reportsFolder is null");
      return null;
    }
    return lambda.apply(config);
  }

  @CheckReturnValue
  private int getRescaledElementWidth(WebElement element, int iframeWidth) {
    int elementWidth = getElementWidth(element);
    if (elementWidth > iframeWidth) {
      return iframeWidth - element.getLocation().getX();
    } else {
      return elementWidth;
    }
  }

  @CheckReturnValue
  private int getRescaledElementHeight(WebElement element, int iframeHeight) {
    int elementHeight = getElementHeight(element);
    if (elementHeight > iframeHeight) {
      return iframeHeight - element.getLocation().getY();
    } else {
      return elementHeight;
    }
  }

  @CheckReturnValue
  private int getRescaledElementWidth(WebElement element, BufferedImage image) {
    if (getElementWidth(element) > image.getWidth()) {
      return image.getWidth() - element.getLocation().getX();
    } else {
      return getElementWidth(element);
    }
  }

  @CheckReturnValue
  private int getRescaledElementHeight(WebElement element, BufferedImage image) {
    if (getElementHeight(element) > image.getHeight()) {
      return image.getHeight() - element.getLocation().getY();
    } else {
      return getElementHeight(element);
    }
  }

  @CheckReturnValue
  private int getRescaledIframeWidth(WebElement iframe, BufferedImage image, WebDriver driver) {
    if (getIframeWidth(driver) > image.getWidth()) {
      return image.getWidth() - iframe.getLocation().getX();
    } else {
      return getIframeWidth(driver);
    }
  }

  @CheckReturnValue
  private int getIframeWidth(WebDriver driver) {
    return ((Long) ((JavascriptExecutor) driver).executeScript("return document.body.clientWidth")).intValue();
  }

  @CheckReturnValue
  private int getElementWidth(WebElement element) {
    return element.getSize().getWidth();
  }

  @CheckReturnValue
  private int getElementHeight(WebElement element) {
    return element.getSize().getHeight();
  }
}
