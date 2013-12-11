package com.codeborne.selenide.impl;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.*;

import static com.codeborne.selenide.Configuration.reportsFolder;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.io.File.separatorChar;
import static org.openqa.selenium.OutputType.FILE;

public class ScreenShotLaboratory {
  public String takeScreenShot(String className, String methodName) {
    return takeScreenShot(getScreenshotFileName(className, methodName));
  }

  protected String getScreenshotFileName(String className, String methodName) {
    return className.replace('.', separatorChar) + separatorChar +
        methodName + '.' + System.currentTimeMillis();
  }

  public String takeScreenShot(String fileName) {
    WebDriver webdriver = getWebDriver();
    if (webdriver == null) {
      System.err.println("Cannot take screenshot because browser is not started");
      return null;
    }

    File targetFile = new File(reportsFolder, fileName + ".html");

    try {
      writeToFile(webdriver.getPageSource(), targetFile);
    } catch (Exception e) {
      System.err.println(e);
    }

    if (webdriver instanceof TakesScreenshot) {
      targetFile = takeScreenshotImage((TakesScreenshot) webdriver, fileName, targetFile);
    }
    else if (webdriver instanceof RemoteWebDriver) {
      WebDriver remoteDriver = new Augmenter().augment(webdriver);
      if (remoteDriver instanceof TakesScreenshot) {
        targetFile = takeScreenshotImage((TakesScreenshot) remoteDriver, fileName, targetFile);
      }
    }

    return targetFile.getAbsolutePath();
  }

  protected File takeScreenshotImage(TakesScreenshot driver, String fileName, final File targetFile) {
    try {
      File scrFile = driver.getScreenshotAs(FILE);
      File imageFile = new File(reportsFolder, fileName + ".png");
      copyFile(scrFile, imageFile);
      return imageFile;
    } catch (Exception e) {
      System.err.println(e);
    }
    return targetFile;
  }

  protected void copyFile(File sourceFile, File targetFile) throws IOException {
    copyFile(new FileInputStream(sourceFile), targetFile);
  }

  protected void copyFile(InputStream in, File targetFile) throws IOException {
    ensureFolderExists(targetFile);

    try {
      final FileOutputStream out = new FileOutputStream(targetFile);
      try {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
          out.write(buffer, 0, len);
        }
      } finally {
        out.close();
      }
    } finally {
      in.close();
    }
  }

  protected void writeToFile(String content, File targetFile) throws IOException {
    copyFile(new ByteArrayInputStream(content.getBytes()), targetFile);
  }

  protected File ensureFolderExists(File targetFile) {
    File folder = targetFile.getParentFile();
    if (!folder.exists()) {
      System.err.println("Creating folder: " + folder);
      if (!folder.mkdirs()) {
        System.err.println("Failed to create " + folder);
      }
    }
    return targetFile;
  }
}
