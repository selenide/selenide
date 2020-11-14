package com.codeborne.selenide;

import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.Screenshot;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Optional;

import static com.codeborne.selenide.WebDriverRunner.driver;

@ParametersAreNonnullByDefault
public class Screenshots {
  public static ScreenShotLaboratory screenshots = ScreenShotLaboratory.getInstance();

  @CheckReturnValue
  public static String saveScreenshotAndPageSource() {
    return screenshots.takeScreenshot(driver()).summary();
  }

  @CheckReturnValue
  @Nonnull
  public static Screenshot takeScreenShot(String className, String methodName) {
    return screenshots.takeScreenShot(driver(), className, methodName);
  }

  /**
   * @deprecated Use either {@link Selenide#screenshot(java.lang.String)} or {@link SelenideDriver#screenshot(java.lang.String)}
   */
  @CheckReturnValue
  @Nullable
  @Deprecated
  public static String takeScreenShot(String fileName) {
    return screenshots.takeScreenShot(driver(), fileName);
  }

  /**
   * Take screenshot and return as a file
   * @return a temporary file, not guaranteed to be stored after tests complete.
   */
  @CheckReturnValue
  @Nullable
  public static File takeScreenShotAsFile() {
    return screenshots.takeScreenShotAsFile(driver());
  }

  /**
   * Take screenshot of the WebElement/SelenideElement
   * @return a temporary file, not guaranteed to be stored after tests complete.
   */
  @CheckReturnValue
  @Nullable
  public static File takeScreenShot(WebElement element) {
    return screenshots.takeScreenshot(driver(), element);
  }

  /**
   * Take screenshot of WebElement/SelenideElement in iframe
   * for partially visible WebElement/Selenide horizontal scroll bar will be present
   * @return a temporary file, not guaranteed to be stored after tests complete.
   */
  @CheckReturnValue
  @Nullable
  public static File takeScreenShot(WebElement iframe, WebElement element) {
    return screenshots.takeScreenshot(driver(), iframe, element);
  }

  /**
   * Take screenshot of WebElement/SelenideElement in iframe
   * for partially visible WebElement/Selenide horizontal scroll bar will be present
   * @return buffered image
   */
  @CheckReturnValue
  @Nullable
  public static BufferedImage takeScreenShotAsImage(WebElement iframe, WebElement element) {
    return screenshots.takeScreenshotAsImage(driver(), iframe, element);
  }

  /**
   * Take screenshot of the WebElement/SelenideElement
   * @return buffered image
   */
  @CheckReturnValue
  @Nullable
  public static BufferedImage takeScreenShotAsImage(WebElement element) {
    return screenshots.takeScreenshotAsImage(driver(), element);
  }

  public static void startContext(String className, String methodName) {
    screenshots.startContext(className, methodName);
  }

  @Nonnull
  public static List<File> finishContext() {
    return screenshots.finishContext();
  }

  /**
   * Get the last screenshot taken
   *
   * @return null if there were no any screenshots taken
   */
  @CheckReturnValue
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
  @CheckReturnValue
  @Nonnull
  public static Optional<File> getLastThreadScreenshot() {
    return screenshots.getLastThreadScreenshot();
  }

  /**
   * Get the last screenshot taken in current {@code context} thread
   *
   * @return {@link java.util.Optional} with screenshot of current {@code context} thread,
   * or an empty Optional if there were no any screenshots taken.
   */
  @CheckReturnValue
  @Nonnull
  public static Optional<File> getLastContextScreenshot() {
    return screenshots.getLastContextScreenshot();
  }
}
