package it.mobile.android.driverproviders.ci;

public class AndroidDriverWithSwagLabs extends AndroidDriverProvider {
  public static final String appId = "com.saucelabs.mydemoapp.rn";
  @Override
  protected String getApplicationUnderTest() {
    return "githubactions_qxmgVeB/Android_SwagLabs";
  }
}
