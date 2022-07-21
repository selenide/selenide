package com.codeborne.selenide.appium;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

public class SampleApp {
  public static File downloadAndroidApp() {
    return downloadSampleApp("ApiDemos-debug.apk");
  }

  public static File downloadIosApp() {
    return downloadSampleApp("TestApp.app.zip");
  }

  private static File downloadSampleApp(String filename) {
    File app = new File("build/apps", filename);
    if (app.exists()) {
      return app;
    }

    if (!app.getParentFile().exists() && !app.getParentFile().mkdirs()) {
      throw new RuntimeException("Failed to create dir " + app.getParentFile().getAbsolutePath());
    }

    String url = "https://github.com/appium/appium/raw/master/packages/appium/sample-code/apps/" + filename;
    try (InputStream in = new URL(url).openStream()) {
      copyInputStreamToFile(in, app);
    } catch (IOException e) {
      throw new AssertionError("Failed to download " + filename + " to " + app.getAbsolutePath(), e);
    }
    return app;
  }
}
