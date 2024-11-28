package it.mobile.ios.driverproviders;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static it.mobile.BrowserstackUtils.browserstackUrl;
import static it.mobile.BrowserstackUtils.getBrowserstackOptions;

public class IosDriverProvider implements WebDriverProvider {
  private static final Logger log = LoggerFactory.getLogger(IosDriverProvider.class);

  @Override
  public WebDriver createDriver(Capabilities capabilities) {
    XCUITestOptions options = getXcuiTestOptions();
    try {
      return new IOSDriver(browserstackUrl(), options);
    } catch (SessionNotCreatedException e) {
      log.error("Failed to start Session", e);
      return new IOSDriver(browserstackUrl(), options);
    }
  }

  protected XCUITestOptions getXcuiTestOptions() {
    XCUITestOptions options = new XCUITestOptions();
    options.setApp("githubactions_qxmgVeB/IOS_SwagLabs");
    options.setPlatformVersion("17");
    options.setDeviceName("iPhone 15 Pro");
    options.setFullReset(false);
    options.setShouldTerminateApp(true);
    options.setCapability("bstack:options", getBrowserstackOptions());
    return options;
  }

}
