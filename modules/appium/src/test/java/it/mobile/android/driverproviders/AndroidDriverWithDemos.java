package it.mobile.android.driverproviders;

import static it.mobile.Apps.downloadApiDemosAndroidApp;

import java.io.File;

public class AndroidDriverWithDemos extends AndroidDriverProvider {
  @Override
  protected File getApplicationUnderTest() {
    return downloadApiDemosAndroidApp();
  }
}
