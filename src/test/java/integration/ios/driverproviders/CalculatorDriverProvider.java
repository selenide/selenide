package integration.ios.driverproviders;

import static integration.Apps.downloadIosApp;

import io.appium.java_client.ios.options.XCUITestOptions;
import java.io.File;
import java.time.Duration;

public class CalculatorDriverProvider extends IosDriverProvider {
  @Override
  protected File getApplicationUnderTest() {
    return downloadIosApp();
  }

  protected XCUITestOptions getXcuiTestOptions() {
    XCUITestOptions options = new XCUITestOptions();
    // on github actions first test run maybe extremely slow
    options.setWdaLaunchTimeout(Duration.ofMinutes(10));
    options.setDeviceName("iPhone 12");
    options.setFullReset(false);
    return options;
  }
}
