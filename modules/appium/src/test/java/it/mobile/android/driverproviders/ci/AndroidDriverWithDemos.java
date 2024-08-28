package it.mobile.android.driverproviders.ci;

public class AndroidDriverWithDemos extends AndroidDriverProvider {
  public static final String appId = "io.appium.android.apis";
  @Override
  protected String getApplicationUnderTest() {
    return "bs://bdef1c5bb7e1d5387a5b3da0557e66b48456f5d3";
  }
}
