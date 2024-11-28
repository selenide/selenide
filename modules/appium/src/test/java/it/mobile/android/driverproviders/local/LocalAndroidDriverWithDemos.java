package it.mobile.android.driverproviders.local;

import java.io.File;

import static it.mobile.Apps.downloadApiDemosAndroidApp;

public class LocalAndroidDriverWithDemos extends LocalAndroidDriverProvider {
  @Override
  protected File getApplicationUnderTest() {
    return downloadApiDemosAndroidApp();
  }
}
