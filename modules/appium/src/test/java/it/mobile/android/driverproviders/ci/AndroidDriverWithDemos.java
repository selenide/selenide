package it.mobile.android.driverproviders.ci;

public class AndroidDriverWithDemos extends AndroidDriverProvider {
  public static final String appId = "io.appium.android.apis";
  @Override
  protected String getApplicationUnderTest() {
    return "githubactions_qxmgVeB/ApiDemos";
  }
}
