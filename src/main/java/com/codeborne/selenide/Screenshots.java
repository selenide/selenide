package com.codeborne.selenide;

import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.util.List;

public class Screenshots {
  public static ScreenShotLaboratory screenshots = new ScreenShotLaboratory();

  public static String takeScreenShot(String className, String methodName) {
    return screenshots.takeScreenShot(className, methodName);
  }

  public static String takeScreenShot(String fileName) {
    return screenshots.takeScreenShot(fileName);
  }

  /**
   * Take screenshot and return as a file
   * @return a temporary file, not guaranteed to be stored after tests complete.
   */
  public static File takeScreenShotAsFile() {
    return screenshots.takeScreenShotAsFile();
  }

  public static File takeScreenShot(WebElement element) {
    return screenshots.takeScreenshot(element);
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
