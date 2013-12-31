package com.codeborne.selenide;

import com.codeborne.selenide.impl.ScreenShotLaboratory;

import java.util.List;

public class Screenshots {
  public static ScreenShotLaboratory screenshots = new ScreenShotLaboratory();

  public static String takeScreenShot(String className, String methodName) {
    return screenshots.takeScreenShot(className, methodName);
  }

  public static String takeScreenShot(String fileName) {
    return screenshots.takeScreenShot(fileName);
  }

  public static String takeScreenShot() {
    return screenshots.takeScreenShot();
  }

  public static void startContext(String className, String methodName) {
    screenshots.startContext(className, methodName);
  }

  public static List<String> finishContext() {
    return screenshots.finishContext();
  }
}
