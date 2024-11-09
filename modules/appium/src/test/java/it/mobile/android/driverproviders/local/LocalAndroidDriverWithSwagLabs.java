package it.mobile.android.driverproviders.local;

import java.io.File;

import static it.mobile.Apps.downloadSwagLabsAndroidApp;

public class LocalAndroidDriverWithSwagLabs extends LocalAndroidDriverProvider {
  @Override
  protected File getApplicationUnderTest() {
    return downloadSwagLabsAndroidApp();
  }
}
