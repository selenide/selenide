package com.codeborne.selenide.appium.ios_test_app;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.webdriver.DriverFactory;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static com.codeborne.selenide.appium.SampleApp.downloadIosApp;

class IosTestAppDriverFactory implements DriverFactory {
  @Override
  public void setupWebdriverBinary() {
  }

  @Nonnull
  @Override
  public XCUITestOptions createCapabilities(Config config, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    File app = downloadIosApp();
    XCUITestOptions options = new XCUITestOptions();
    options.setDeviceName("iPhone 12");
    options.setApp(app.getAbsolutePath());
    options.setFullReset(false);
    return options;
  }

  @Nonnull
  @Override
  public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    XCUITestOptions options = createCapabilities(config, browser, proxy, browserDownloadsFolder);
    try {
      return new IOSDriver(new URL("http://127.0.0.1:4723/wd/hub"), options);
    }
    catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
