package com.codeborne.selenide.appium.demos;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.remote.AutomationName;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.openqa.selenium.remote.CapabilityType.APPLICATION_NAME;

public class AndroidDriverWithDemos implements WebDriverProvider {
  @CheckReturnValue
  @Nonnull
  @Override
  public WebDriver createDriver(@Nonnull Capabilities capabilities) {
    File app = downloadApk();

    UiAutomator2Options options = new UiAutomator2Options();
    options.merge(capabilities);
    options.setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2);
    options.setPlatformName("Android");
    options.setDeviceName("Android Emulator");
    options.setPlatformVersion("4.4.2");
    options.setCapability(APPLICATION_NAME, "Appium");
    options.setApp(app.getAbsolutePath());
    options.setAppPackage("io.appium.android.apis");
    options.setAppActivity(".ApiDemos");

    try {
      return new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), options);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private File downloadApk() {
    File apk = new File("build/ApiDemos-debug.apk");
    if (!apk.exists()) {
      String url = "https://github.com/appium/sample-code/blob/master/sample-code/apps/ApiDemos/bin/ApiDemos-debug.apk?raw=true";
      try (InputStream in = new URL(url).openStream()) {
        copyInputStreamToFile(in, apk);
      } catch (IOException e) {
        throw new AssertionError("Failed to download apk", e);
      }
    }
    return apk;
  }
}
