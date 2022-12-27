package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.OutputType;
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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
import java.util.stream.Stream;

import static com.codeborne.selenide.impl.FileHelper.ensureParentFolderExists;
import static com.codeborne.selenide.impl.Plugins.inject;
import static java.io.File.separatorChar;
import static java.lang.ThreadLocal.withInitial;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
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

  private final Photographer photographer;
  private final PageSourceExtractor extractor;
  private final Clock clock;
  protected final List<Screenshot> allScreenshots = new ArrayList<>();
  protected AtomicLong screenshotCounter = new AtomicLong();

  protected final ThreadLocal<String> currentContext = withInitial(() -> "");
  protected final ThreadLocal<List<Screenshot>> currentContextScreenshots = new ThreadLocal<>();
  protected final ThreadLocal<List<Screenshot>> threadScreenshots = withInitial(ArrayList::new);

  protected ScreenShotLaboratory() {
    this(inject(Photographer.class), inject(PageSourceExtractor.class), new Clock());
  }

  protected ScreenShotLaboratory(Photographer photographer, PageSourceExtractor extractor, Clock clock) {
    this.photographer = photographer;
    this.extractor = extractor;
    this.clock = clock;
  }

  @CheckReturnValue
  @Nonnull
  public Screenshot takeScreenShot(Driver driver, String className, String methodName) {
    return takeScreenshot(driver, getScreenshotFileName(className, methodName), true, true);
  }

  @CheckReturnValue
  @Nonnull
  protected String getScreenshotFileName(String className, String methodName) {
    return className.replace('.', separatorChar) + separatorChar +
      methodName + '.' + clock.timestamp();
  }

  /**
   * Takes screenshot of current browser window.
   * Stores 2 files:
   * 1. html of the page (if "savePageSource" parameter is true), and
   * 2. screenshot of the page in PNG format (if "saveScreenshot" parameter is true)
   * <p>
   * Either file may be null if webdriver has failed to save it.
   *
   * @param fileName name of file (without extension) to store screenshot to.
   * @return instance of {@link Screenshot} containing both files
   */
  @CheckReturnValue
  @Nonnull
  public Screenshot takeScreenshot(Driver driver, String fileName, boolean saveScreenshot, boolean savePageSource) {
    Screenshot screenshot = ifWebDriverStarted(driver, webDriver ->
      ifReportsFolderNotNull(driver.config(), config ->
        takeScreenShot(config, driver, fileName, saveScreenshot, savePageSource)));
    return screenshot != null ? screenshot : Screenshot.none();
  }

  @CheckReturnValue
  @Nullable
  public <T> T takeScreenShot(Driver driver, OutputType<T> outputType) {
    return ifWebDriverStarted(driver, webDriver ->
      photographer.takeScreenshot(driver, outputType)
        .map(screenshot -> addToHistoryIfFile(driver.config(), screenshot, outputType))
        .orElse(null));
  }

  private <T> T addToHistoryIfFile(Config config, T screenshot, OutputType<T> outputType) {
    if (outputType == OutputType.FILE) {
      addToImageHistory(config, (File) screenshot);
    }
    return screenshot;
  }

  @CheckReturnValue
  @Nonnull
  private Screenshot takeScreenShot(Config config, Driver driver, String fileName, boolean saveScreenshot, boolean savePageSource) {
    File source = savePageSource ? savePageSourceToFile(config, fileName, driver) : null;
    File image = saveScreenshot ? savePageImageToFile(config, fileName, driver) : null;
    Screenshot screenshot = new Screenshot(image, toUrl(config, image), toUrl(config, source));
    addToHistory(screenshot);
    return screenshot;
  }

  @CheckReturnValue
  @Nullable
  public File takeScreenshot(Driver driver, WebElement element) {
    try {
      BufferedImage destination = takeScreenshotAsImage(driver, element);
      if (destination != null) {
        return writeToFile(driver, destination);
      }
    }
    catch (IOException e) {
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

    try {
      byte[] screenshot = element.getScreenshotAs(BYTES);
      return Optional.of(imageFromBytes(screenshot));
    }
    catch (InvalidElementStateException elementIsNotVisible) {
      log.error("Failed to take element screenshot: {}", elementIsNotVisible.toString());
      return Optional.empty();
    }
  }

  private BufferedImage imageFromBytes(byte[] screenshot) {
    try (InputStream in = new ByteArrayInputStream(screenshot)) {
      return ImageIO.read(in);
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to take element screenshot", e);
    }
  }

  @CheckReturnValue
  @Nonnull
  protected String generateScreenshotFileName() {
    return currentContext.get() + clock.timestamp() + "." + screenshotCounter.getAndIncrement();
  }

  @CheckReturnValue
  @Nullable
  public File takeScreenshot(Driver driver, WebElement iframe, SelenideElement element) {
    try {
      BufferedImage destination = takeScreenshotAsImage(driver, iframe, element);
      if (destination != null) {
        return writeToFile(driver, destination);
      }
    }
    catch (IOException e) {
      log.error("Failed to take screenshot of {} inside frame {}", element, iframe, e);
    }
    return null;
  }

  @CheckReturnValue
  @Nonnull
  private File writeToFile(Driver driver, BufferedImage destination) throws IOException {
    File screenshotOfElement = new File(driver.config().reportsFolder(), generateScreenshotFileName() + ".png").getAbsoluteFile();
    ensureParentFolderExists(screenshotOfElement);
    ImageIO.write(destination, "png", screenshotOfElement);
    return screenshotOfElement;
  }

  @CheckReturnValue
  @Nullable
  public BufferedImage takeScreenshotAsImage(Driver driver, WebElement iframe, SelenideElement element) {
    return ifWebDriverStarted(driver, webdriver ->
      ifReportsFolderNotNull(driver.config(), config ->
        takeElementScreenshotAsImage(driver, iframe, element)
      ));
  }

  @Nullable
  @CheckReturnValue
  private BufferedImage takeElementScreenshotAsImage(Driver driver, WebElement iframe, SelenideElement element) {
    driver.switchTo().frame(iframe);
    try {
      WebElement webElement = element.toWebElement();
      return takeElementScreenshotAsImage(driver, webElement).orElse(null);
    }
    finally {
      driver.switchTo().parentFrame();
    }
  }

  @CheckReturnValue
  @Nullable
  public File takeScreenShotAsFile(Driver driver) {
    return ifWebDriverStarted(driver, webDriver -> {
      try {
        return photographer.takeScreenshot(driver, FILE)
          .map(imageFile -> addToImageHistory(driver.config(), imageFile))
          .orElse(null);
      }
      catch (Exception e) {
        log.error("Failed to take screenshot in memory", e);
        return null;
      }
    });
  }

  protected void addToHistory(Screenshot screenshot) {
    if (currentContextScreenshots.get() != null) {
      currentContextScreenshots.get().add(screenshot);
    }
    synchronized (allScreenshots) {
      allScreenshots.add(screenshot);
    }
    threadScreenshots.get().add(screenshot);
  }

  @Nonnull
  private File addToImageHistory(Config config, File imageFile) {
    Screenshot screenshot = new Screenshot(imageFile, toUrl(config, imageFile), null);
    if (currentContextScreenshots.get() != null) {
      currentContextScreenshots.get().add(screenshot);
    }
    synchronized (allScreenshots) {
      allScreenshots.add(screenshot);
    }
    threadScreenshots.get().add(screenshot);
    return imageFile;
  }

  @CheckReturnValue
  @Nullable
  protected File savePageImageToFile(Config config, String fileName, Driver driver) {
    try {
      Optional<byte[]> srcFile = photographer.takeScreenshot(driver, BYTES);
      if (!srcFile.isPresent()) {
        log.info("Webdriver doesn't support screenshots");
        return null;
      }
      File imageFile = new File(config.reportsFolder(), fileName + ".png").getAbsoluteFile();
      writeToFileSafely(srcFile.get(), imageFile);
      return imageFile;
    }
    catch (WebDriverException e) {
      log.error("Failed to take screenshot to {}", fileName, e);
      return null;
    }
  }

  private static void writeToFileSafely(byte[] srcFile, File imageFile) {
    try {
      FileUtils.writeByteArrayToFile(imageFile, srcFile);
    }
    catch (IOException e) {
      log.error("Failed to save screenshot to {}", imageFile, e);
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
  public List<Screenshot> finishContext() {
    List<Screenshot> result = currentContextScreenshots.get();
    currentContext.set("");
    currentContextScreenshots.remove();
    return result;
  }

  @CheckReturnValue
  @Nonnull
  public List<File> getScreenshots() {
    synchronized (allScreenshots) {
      return allScreenshots.stream()
        .map(screenshot -> screenshot.getImageFile())
        .filter(image -> image != null)
        .collect(toList());
    }
  }

  @CheckReturnValue
  @Nonnull
  public List<File> getThreadScreenshots() {
    List<Screenshot> screenshots = threadScreenshots.get();
    return streamOf(screenshots)
      .map(screenshot -> screenshot.getImageFile())
      .collect(toList());
  }

  @CheckReturnValue
  @Nonnull
  public List<Screenshot> threadScreenshots() {
    List<Screenshot> screenshots = threadScreenshots.get();
    return screenshots == null ? emptyList() : screenshots;
  }

  @CheckReturnValue
  @Nonnull
  public List<File> getContextScreenshots() {
    List<Screenshot> screenshots = currentContextScreenshots.get();
    return streamOf(screenshots)
      .map(screenshot -> screenshot.getImageFile())
      .collect(toList());
  }

  @CheckReturnValue
  @Nonnull
  public List<Screenshot> contextScreenshots() {
    List<Screenshot> screenshots = currentContextScreenshots.get();
    return screenshots == null ? emptyList() : screenshots;
  }

  @CheckReturnValue
  @Nullable
  public File getLastScreenshot() {
    Screenshot screenshot = lastScreenshot();
    return screenshot == null ? null : screenshot.getImageFile();
  }

  @CheckReturnValue
  @Nullable
  public Screenshot lastScreenshot() {
    synchronized (allScreenshots) {
      return allScreenshots.isEmpty() ? null : allScreenshots.get(allScreenshots.size() - 1);
    }
  }

  @CheckReturnValue
  @Nonnull
  public Optional<File> getLastThreadScreenshot() {
    List<File> screenshots = getThreadScreenshots();
    return getLastScreenshot(screenshots);
  }

  @CheckReturnValue
  @Nonnull
  public Optional<Screenshot> lastThreadScreenshot() {
    List<Screenshot> screenshots = threadScreenshots();
    return lastScreenshot(screenshots);
  }

  @CheckReturnValue
  @Nonnull
  public Optional<File> getLastContextScreenshot() {
    List<File> screenshots = getContextScreenshots();
    return getLastScreenshot(screenshots);
  }

  @CheckReturnValue
  @Nonnull
  public Optional<Screenshot> lastContextScreenshot() {
    List<Screenshot> screenshots = contextScreenshots();
    return lastScreenshot(screenshots);
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
  private Optional<Screenshot> lastScreenshot(@Nullable List<Screenshot> screenshots) {
    return screenshots == null || screenshots.isEmpty()
      ? Optional.empty()
      : Optional.of(screenshots.get(screenshots.size() - 1));
  }

  @CheckReturnValue
  @Nonnull
  public Screenshot takeScreenshot(Driver driver, boolean saveScreenshot, boolean savePageSource) {
    Screenshot screenshot = ifWebDriverStarted(driver, webDriver ->
      ifReportsFolderNotNull(driver.config(), config ->
        takeScreenShot(config, driver, generateScreenshotFileName(), saveScreenshot, savePageSource)));
    return screenshot != null ? screenshot : Screenshot.none();
  }

  @CheckReturnValue
  @Nullable
  private String toUrl(Config config, @Nullable File file) {
    if (file == null) {
      return null;
    }
    else if (config.reportsUrl() != null) {
      return formatScreenShotURL(config.reportsUrl(), file.getAbsolutePath());
    }
    try {
      return file.getCanonicalFile().toURI().toURL().toExternalForm();
    }
    catch (IOException e) {
      return "file://" + file.getAbsolutePath();
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
    }
    else {
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
    }
    catch (UnsupportedEncodingException e) {
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

  private static <T> Stream<T> streamOf(@Nullable List<T> list) {
    return list == null ? Stream.empty() : list.stream();
  }
}
