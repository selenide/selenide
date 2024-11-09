package com.codeborne.selenide;

import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.Screenshot;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Optional;

import static com.codeborne.selenide.WebDriverRunner.driver;

public class Screenshots {
  public static ScreenShotLaboratory screenshots = ScreenShotLaboratory.getInstance();

  public static String saveScreenshotAndPageSource() {
    Driver driver = driver();
    Config config = driver.config();
    return screenshots.takeScreenshot(driver, config.screenshots(), config.savePageSource()).summary();
  }

  public static Screenshot takeScreenShot(String className, String methodName) {
    return screenshots.takeScreenShot(driver(), className, methodName);
  }

  /**
   * Take screenshot and return as a file
   * @return a temporary file, not guaranteed to be stored after tests complete.
   */
  @Nullable
  public static File takeScreenShotAsFile() {
    return screenshots.takeScreenShotAsFile(driver());
  }

  /**
   * Take screenshot of the WebElement/SelenideElement
   * @return a temporary file, not guaranteed to be stored after tests complete.
   */
  public static File takeScreenShot(WebElement element) {
    return screenshots.takeScreenshot(driver(), element);
  }

  /**
   * Take screenshot of WebElement/SelenideElement in iframe
   * for partially visible WebElement/Selenide horizontal scroll bar will be present
   * @return a temporary file, not guaranteed to be stored after tests complete.
   */
  @Nullable
  public static File takeScreenShot(WebElement iframe, SelenideElement element) {
    return screenshots.takeScreenshot(driver(), iframe, element);
  }

  /**
   * Take screenshot of WebElement/SelenideElement in iframe
   * for partially visible WebElement/Selenide horizontal scroll bar will be present
   * @return buffered image
   */
  @Nullable
  public static BufferedImage takeScreenShotAsImage(WebElement iframe, SelenideElement element) {
    return screenshots.takeScreenshotAsImage(driver(), iframe, element);
  }

  /**
   * Take screenshot of the WebElement/SelenideElement
   * @return buffered image
   */
  public static BufferedImage takeScreenShotAsImage(WebElement element) {
    return screenshots.takeScreenshotAsImage(driver(), element);
  }

  public static void startContext(String className, String methodName) {
    screenshots.startContext(className, methodName);
  }

  @CanIgnoreReturnValue
  public static List<Screenshot> finishContext() {
    return screenshots.finishContext();
  }

  /**
   * Get the last screenshot taken
   *
   * @return null if there were no any screenshots taken
   */
  @Nullable
  public static File getLastScreenshot() {
    return screenshots.getLastScreenshot();
  }

  /**
   * Get the last screenshot taken in current thread
   *
   * @return {@link java.util.Optional} with screenshot of current thread,
   * or an empty Optional if there were no any screenshots taken.
   */
  public static Optional<File> getLastThreadScreenshot() {
    return screenshots.getLastThreadScreenshot();
  }

  /**
   * Get the last screenshot taken in current {@code context} thread
   *
   * @return {@link java.util.Optional} with screenshot of current {@code context} thread,
   * or an empty Optional if there were no any screenshots taken.
   */
  public static Optional<File> getLastContextScreenshot() {
    return screenshots.getLastContextScreenshot();
  }
}
