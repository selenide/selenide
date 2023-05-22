package integration.android.driverproviders;

import static integration.Apps.downloadApiDemosAndroidApp;

import java.io.File;

public class AndroidDriverWithDemos extends AndroidDriverProvider {
  @Override
  protected File getApplicationUnderTest() {
    return downloadApiDemosAndroidApp();
  }
}
