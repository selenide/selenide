package com.codeborne.selenide;

import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.openqa.selenium.WebElement;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import static com.codeborne.selenide.WebDriverRunner.context;

public class Screenshots {
  public static ScreenShotLaboratory screenshots = new ScreenShotLaboratory();

  public static String takeScreenShot(String className, String methodName) {
    return screenshots.takeScreenShot(context(), className, methodName);
  }

  /**
   * Take screenshot and give it filename
   *
   * @return absolute path of the screenshot taken
   */
  public static String takeScreenShot(String fileName) {
    return screenshots.takeScreenShot(context(), fileName);
  }

  /**
   * Take screenshot and return as a file
   * @return a temporary file, not guaranteed to be stored after tests complete.
   */
  public static File takeScreenShotAsFile() {
    return screenshots.takeScreenShotAsFile(context());
  }

  /**
   * Take screenshot of the WebElement/SelenideElement
   * @return a temporary file, not guaranteed to be stored after tests complete.
   */
  public static File takeScreenShot(WebElement element) {
    return screenshots.takeScreenshot(context(), element);
  }

  /**
   * Take screenshot of WebElement/SelenideElement in iframe
   * for partially visible WebElement/Selenide horizontal scroll bar will be present
   * @return a temporary file, not guaranteed to be stored after tests complete.
   */
  public static File takeScreenShot(WebElement iframe, WebElement element) {
    return screenshots.takeScreenshot(context(), iframe, element);
  }

  /**
   * Take screenshot of WebElement/SelenideElement in iframe
   * for partially visible WebElement/Selenide horizontal scroll bar will be present
   * @return buffered image
   */
  public static BufferedImage takeScreenShotAsImage(WebElement iframe, WebElement element) {
    return screenshots.takeScreenshotAsImage(context(), iframe, element);
  }

  /**
   * Take screenshot of the WebElement/SelenideElement
   * @return buffered image
   */
  public static BufferedImage takeScreenShotAsImage(WebElement element) {
    return screenshots.takeScreenshotAsImage(context(), element);
  }

  public static void startContext(String className, String methodName) {
    screenshots.startContext(className, methodName);
  }

  public static List<File> finishContext() {
    return screenshots.finishContext();
  }

  /**
   * Get the last screenshot taken
   * @return null if there were no any screenshots taken
   */
  public static File getLastScreenshot() {
    return screenshots.getLastScreenshot();
  }
}
