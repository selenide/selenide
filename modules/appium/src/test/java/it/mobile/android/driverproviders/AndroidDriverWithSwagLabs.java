package it.mobile.android.driverproviders;

import it.mobile.Apps;
import java.io.File;

public class AndroidDriverWithSwagLabs extends AndroidDriverProvider {

  @Override
  protected File getApplicationUnderTest() {
    return Apps.downloadSwagLabsAndroidApp();
  }
}
