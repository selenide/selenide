package it.mobile.ios.driverproviders;

import static it.mobile.Apps.downloadSwagLabsIosApp;

import java.io.File;

public class SwagLabsIosDriverProvider extends IosDriverProvider {
  @Override
  protected File getApplicationUnderTest() {
    return downloadSwagLabsIosApp();
  }
}
