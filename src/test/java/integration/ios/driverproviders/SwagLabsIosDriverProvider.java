package integration.ios.driverproviders;

import static integration.Apps.downloadSwagLabsIosApp;

import java.io.File;

public class SwagLabsIosDriverProvider extends IosDriverProvider {
  @Override
  protected File getApplicationUnderTest() {
    return downloadSwagLabsIosApp();
  }
}
