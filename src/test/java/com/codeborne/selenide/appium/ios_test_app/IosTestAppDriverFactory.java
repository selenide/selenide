package com.codeborne.selenide.appium.ios_test_app;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.webdriver.DriverFactory;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static io.appium.java_client.remote.AutomationName.IOS_XCUI_TEST;

class IosTestAppDriverFactory implements DriverFactory {
  @Override
  public void setupWebdriverBinary() {
  }

  @Nonnull
  @Override
  public UiAutomator2Options createCapabilities(Config config, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    File app = downloadApp();
    UiAutomator2Options options = new UiAutomator2Options();
    options.setAutomationName(IOS_XCUI_TEST);
    options.setPlatformName("iOS");
    options.setPlatformVersion("15.5");
    options.setDeviceName("iPhone 12");
    options.setApp(app.getAbsolutePath());
    options.setFullReset(false);
    return options;
  }

  @Nonnull
  @Override
  public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    UiAutomator2Options options = createCapabilities(config, browser, proxy, browserDownloadsFolder);
    try {
      return new IOSDriver(new URL("http://127.0.0.1:4723/wd/hub"), options);
    }
    catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private File downloadApp() {
    try {
      File app = new File("build/apps/TestApp.app.zip");
      if (app.exists()) return app;
      if (!app.getParentFile().exists() && !app.getParentFile().mkdirs()) {
        throw new RuntimeException("Failed to create dir " + app.getParentFile().getAbsolutePath());
      }
      IOUtils.copy(new URL("https://github.com/appium/appium/raw/master/sample-code/apps/TestApp.app.zip"), app);
      return app;
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
