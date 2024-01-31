package it.mobile.ios.driverproviders;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public abstract class IosDriverProvider implements WebDriverProvider {
  private static final Logger log = LoggerFactory.getLogger(IosDriverProvider.class);

  @Nonnull
  @Override
  public WebDriver createDriver(@Nonnull Capabilities capabilities) {
    XCUITestOptions options = getXcuiTestOptions();
    options.setApp(getApplicationUnderTest().getAbsolutePath());
    try {
      return new IOSDriver(url(), options);
    } catch (SessionNotCreatedException e) {
      log.error("Failed to start Simulator", e);
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
    options.setDeviceName("iPhone 15");
    options.setFullReset(false);
    options.setShouldTerminateApp(true);
    return options;
  }

  private static URL url() {
    try {
      return new URL("http://127.0.0.1:4723/");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
