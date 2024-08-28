package it.mobile.android.driverproviders.local;

import java.io.File;

import static it.mobile.Apps.downloadSwagLabsAndroidApp;

public class LocalAndroidDriverWithSwagLabs extends LocalAndroidDriverProvider {
  public static final String appId = "com.saucelabs.mydemoapp.rn";
  @Override
  protected File getApplicationUnderTest() {
    return downloadSwagLabsAndroidApp();
  }
}
