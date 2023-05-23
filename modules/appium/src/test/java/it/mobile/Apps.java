package it.mobile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

public class Apps {
  private static final Logger log = LoggerFactory.getLogger(Apps.class);
  private static final String APPIUM_APPS_URL = "https://github.com/appium/appium/raw/master/packages/appium/sample-code/apps/";
  private static final String SAUCE_LAB_APPS_URL = "https://github.com/saucelabs/my-demo-app-rn/releases/download/v1.3.0/";

  public static File downloadApiDemosAndroidApp() {
    return downloadSampleApp(APPIUM_APPS_URL, "ApiDemos-debug.apk");
  }

  public static File downloadIosApp() {
    return downloadSampleApp(APPIUM_APPS_URL, "TestApp.app.zip");
  }

  //can be used later if needed
  public static File downloadSwagLabsAndroidApp() {
    return downloadSampleApp(SAUCE_LAB_APPS_URL, "Android-MyDemoAppRN.1.3.0.build-244.apk");
  }

  public static File downloadSwagLabsIosApp() {
    return downloadSampleApp(SAUCE_LAB_APPS_URL, "iOS-Simulator-MyRNDemoApp.1.3.0-162.zip");
  }

  private static File downloadSampleApp(String remoteFilePath, String filename) {
    File app = new File("build/apps", filename);
    if (app.exists()) {
      log.info("Using pre-downloaded app: {}", app.getAbsolutePath());
      return app;
    }

    if (!app.getParentFile().exists() && !app.getParentFile().mkdirs()) {
      throw new RuntimeException("Failed to create dir " + app.getParentFile().getAbsolutePath());
    }

    String url = remoteFilePath + filename;
    log.info("Downloading app {} to {}...", url, app.getAbsolutePath());
    try (InputStream in = new URL(url).openStream()) {
      copyInputStreamToFile(in, app);
    } catch (IOException e) {
      throw new RuntimeException("Failed to download " + filename + " to " + app.getAbsolutePath(), e);
    }
    return app;
  }
}
