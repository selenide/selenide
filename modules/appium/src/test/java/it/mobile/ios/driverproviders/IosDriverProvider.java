package it.mobile.ios.driverproviders;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import com.codeborne.selenide.webdriver.HttpClientTimeouts;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public abstract class IosDriverProvider implements WebDriverProvider {
  @Nonnull
  @Override
  public WebDriver createDriver(@Nonnull Capabilities capabilities) {
    Configuration.remoteConnectionTimeout = Duration.ofSeconds(10).toMillis();
    Configuration.remoteReadTimeout = Duration.ofSeconds(30).toMillis();
    HttpClientTimeouts.defaultLocalReadTimeout = Duration.ofSeconds(20);
    XCUITestOptions options = getXcuiTestOptions();
    options.setApp(getApplicationUnderTest().getAbsolutePath());
    try {
      return new IOSDriver(url(), options);
    } catch (SessionNotCreatedException e) {
      // Sometimes WDA session creation freezes unexpectedly on CI
      options.useNewWDA();
      return new IOSDriver(url(), options);
    }
  }

  protected abstract File getApplicationUnderTest();

  protected XCUITestOptions getXcuiTestOptions() {
    XCUITestOptions options = new XCUITestOptions();
    // on GitHub actions, first test run maybe extremely slow
    options.setWdaLaunchTimeout(Duration.ofMinutes(10));
    options.setDeviceName("iPhone 14");
    options.setFullReset(false);
    return options;
  }

  private static URL url() {
    try {
      return new URL("http://127.0.0.1:4723/wd/hub");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
