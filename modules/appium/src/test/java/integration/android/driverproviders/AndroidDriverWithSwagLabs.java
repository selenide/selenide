package integration.android.driverproviders;

import integration.Apps;
import java.io.File;

public class AndroidDriverWithSwagLabs extends AndroidDriverProvider {

  @Override
  protected File getApplicationUnderTest() {
    return Apps.downloadSwagLabsAndroidApp();
  }
}
