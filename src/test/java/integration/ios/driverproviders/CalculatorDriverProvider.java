package integration.ios.driverproviders;

import java.io.File;

import static integration.Apps.downloadIosApp;

public class CalculatorDriverProvider extends IosDriverProvider {
  @Override
  protected File getApplicationUnderTest() {
    return downloadIosApp();
  }
}
