package it.mobile.ios.driverproviders;

import java.io.File;

import static it.mobile.Apps.downloadIosApp;

public class CalculatorDriverProvider extends IosDriverProvider {
  @Override
  protected File getApplicationUnderTest() {
    return downloadIosApp();
  }
}
