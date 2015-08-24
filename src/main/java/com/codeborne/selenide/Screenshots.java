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
  
  public static File getScreenShotAsFile() {
    return screenshots.getScreenShotAsFile();
  }

  public static File takeScreenShot(WebElement element) {
    return screenshots.takeScreenshot(element);
  }

  public static void startContext(String className, String methodName) {
    screenshots.startContext(className, methodName);
  }

  public static List<String> finishContext() {
    return screenshots.finishContext();
  }
}
