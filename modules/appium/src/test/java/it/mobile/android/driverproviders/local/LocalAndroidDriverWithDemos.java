package it.mobile.android.driverproviders.local;

import java.io.File;

import static it.mobile.Apps.downloadApiDemosAndroidApp;

public class LocalAndroidDriverWithDemos extends LocalAndroidDriverProvider {
  public static final String appId = "io.appium.android.apis";
  @Override
  protected File getApplicationUnderTest() {
    return downloadApiDemosAndroidApp();
  }
}
