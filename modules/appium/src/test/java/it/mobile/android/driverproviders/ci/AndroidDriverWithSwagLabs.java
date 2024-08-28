package it.mobile.android.driverproviders.ci;

public class AndroidDriverWithSwagLabs extends AndroidDriverProvider {
  public static final String appId = "com.saucelabs.mydemoapp.rn";
  @Override
  protected String getApplicationUnderTest() {
    return "bs://28a1e494b46cc8f3931cddd204d03b9af9c3a890";
  }
}
